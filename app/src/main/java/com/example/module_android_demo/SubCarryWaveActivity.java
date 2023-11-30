package com.example.module_android_demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.function.commfun;
import com.graphics.cls.myview;
import com.tools.DjxlExcel;
import com.tools.dlog;
import com.uhf.api.cls.R2000_calibration;
import com.uhf.api.cls.R2000_calibration.Region;
import com.uhf.api.cls.R2000_calibration.VSWRReturnloss_DATA;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.AntPortsVSWR;
import com.uhf.api.cls.Reader.HoptableData_ST;
import com.uhf.api.cls.Reader.Mtr_Param;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.Region_Conf;
import com.uhf.api.cls.Reader.TAGINFO;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SubCarryWaveActivity extends Activity {
	MyApplication myapp;
	Button button_cwset, button_on, button_off, button_getafpow,
			button_cabwgraph, button_cabwgraph2,button_returnloss,button_allfreturnloss;
	TextView tv_ibpow, tv_ibrpow, tv_ipow, tv_iapow, tv_iarpow, tv_cnt, tv_tep;

	short oemcfgaddr_rfcal_fwdpwr_A2 = 0x00000091;
	short oemcfgaddr_rfcal_fwdpwr_A1 = 0x00000092;
	short oemcfgaddr_rfcal_fwdpwr_A0 = 0x00000093;

	short oemcfgaddr_rfcal_revpwr_A2 = 0x000003C6;
	short oemcfgaddr_rfcal_revpwr_A1 = 0x000003C7;
	short oemcfgaddr_rfcal_revpwr_A0 = 0x000003C8;
	double fa2;
	double fa1;
	double fa0;

	double ra2;
	double ra1;
	double ra0;
	R2000_calibration.MAC_DATA r2000macp = new R2000_calibration().new MAC_DATA(
			(short) 0x0B00);
	R2000_calibration.MAC_DATA r2000macfp = new R2000_calibration().new MAC_DATA(
			(short) 0x0B04);
	R2000_calibration.R2000cmd rcmdo2 = R2000_calibration.R2000cmd.OEMread;
	R2000_calibration.R2000cmd rcmdo = R2000_calibration.R2000cmd.readMAC;
	R2000_calibration r2000pcmd = new R2000_calibration();
	int ant =1, power = 3000, fre = 915250;
	READER_ERR er = READER_ERR.MT_OK_ERR;

	myview drawview;

	int x_w = 55, x_wadd = 30;// x段宽度
	int Height = 600, Width = x_w * 50 + x_wadd;// 坐标轴长宽
	int spit = 5;// 分几段
	int x_st = 60, y_st = 200;// 坐标轴中心
	int x_wbl = 20, x_wsl = 10;// x大段长，x小段长
	int xy_x_char = 8, xy_y_char = 20;// 坐标轴 点 文字位置
	int x_char = 15;// x轴上 文字位置

	public class R2000Tdata {
		public TAGINFO[] trd;
		public double power1;
		public double refpower1;
		public double power2;
		public double power3;
		public double refpower3;
	}

	public class MsgObj {
		public byte[] soh = new byte[1];
		public byte[] dataLen = new byte[1];
		public byte[] opCode = new byte[1];
		public byte[] status = new byte[2];
		public byte[] crc = new byte[2];
		public byte[] data = new byte[250];

		public byte[] getcheckcrcdata() {
			byte[] crcb = new byte[dataLen[0] + 4];
			int p = 0;
			crcb[p] = dataLen[0];
			p++;
			crcb[p++] = opCode[0];
			crcb[p++] = status[0];
			crcb[p++] = status[1];
			for (int i = 0; i < dataLen[0]; i++)
				crcb[p++] = data[i];
			return crcb;
		}
	}

	READER_ERR FlushDummyData2Mod() {
		/* if (m_stream->isOpen) */
		{
			byte[] zerobuf = new byte[255];
			zerobuf[0] = (byte) 0xff;
			zerobuf[1] = (byte) 250;
			zerobuf[2] = 0x0;
			for (int i = 3; i < 255; ++i)
				zerobuf[i] = 0;

			myapp.Mreader.DataTransportSend(zerobuf, 255, 2000);

			return READER_ERR.MT_OK_ERR;
		}

	}

	READER_ERR TestModLive() {

		READER_ERR err = READER_ERR.MT_OK_ERR;
		byte[] cmd = new byte[] { (byte) 0xff, 0x00, 0x03, 0x1d, 0x0c };
		byte[] resp = new byte[50];
		byte[] resp2 = new byte[50];
		myapp.Mreader.DataTransportSend(cmd, cmd.length, 1000);
		if (myapp.Mreader.DataTransportRecv(resp, 5, 1000) == -1)
			return READER_ERR.MT_CMD_FAILED_ERR;
		if (myapp.Mreader.DataTransportRecv(resp2, resp[1] + 2, 1000) == -1)
			return READER_ERR.MT_CMD_FAILED_ERR;
		return err;

	}

	private int SendandRev(byte[] data, int timeout, MsgObj hMsg) {
		int COMM_NON_FATAL_ERR = 0xfefd;
		int MODULE_NEED_RESTART = 0xfefe;
		short MSG_CRC_INIT = (short) 0xFFFF;

		System.out.println("send:" + myapp.Mreader.bytes_Hexstr(data));

		int re = myapp.Mreader.DataTransportSend(data, data.length, timeout);
		if (re != 0)
			return COMM_NON_FATAL_ERR;

		short scrc;
		READER_ERR err = READER_ERR.MT_OK_ERR;
		int ret = 0;

		System.out.print("revd:");
		ret = myapp.Mreader.DataTransportRecv(hMsg.soh, 1, 1000);
		System.out.print(myapp.Mreader.bytes_Hexstr(hMsg.soh));

		if (ret == -2 || ret == -3)
			return COMM_NON_FATAL_ERR;
		else if (ret == -1)
			return READER_ERR.MT_IO_ERR.value();
		else if (ret == -4) {
			// 可能模块处于等待指令状态
			if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
				return READER_ERR.MT_IO_ERR.value();
			}
			if (TestModLive() == READER_ERR.MT_OK_ERR) {
				return COMM_NON_FATAL_ERR;
			} else {
				return MODULE_NEED_RESTART;
			}
		}

		if ((hMsg.soh[0] & 0xff) != 0xff) {
			if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
				return READER_ERR.MT_IO_ERR.value();
			} else {
				return 0xfefd;
			}
		}

		scrc = MSG_CRC_INIT;

		if (myapp.Mreader.DataTransportRecv(hMsg.dataLen, 1, 1000) == -1) {
			return COMM_NON_FATAL_ERR;
		}
		System.out.print(myapp.Mreader.bytes_Hexstr(hMsg.dataLen));

		if (myapp.Mreader.DataTransportRecv(hMsg.opCode, 1, 1000) == -1) {
			return COMM_NON_FATAL_ERR;
		}
		System.out.print(myapp.Mreader.bytes_Hexstr(hMsg.opCode));

		if (myapp.Mreader.DataTransportRecv(hMsg.status, 2, 1000) == -1) {
			return COMM_NON_FATAL_ERR;
		}
		System.out.print(myapp.Mreader.bytes_Hexstr(hMsg.status));

		if (hMsg.dataLen[0] > 0) {
			if (myapp.Mreader.DataTransportRecv(hMsg.data, hMsg.dataLen[0],
					1000) == -1) {
				return COMM_NON_FATAL_ERR;
			}
			byte[] fdata = new byte[hMsg.dataLen[0]];
			System.arraycopy(hMsg.data, 0, fdata, 0, hMsg.dataLen[0]);
			System.out.print(myapp.Mreader.bytes_Hexstr(fdata));
		}

		if (myapp.Mreader.DataTransportRecv(hMsg.crc, 2, 1000) == -1) {
			return COMM_NON_FATAL_ERR;
		}
		System.out.println(myapp.Mreader.bytes_Hexstr(hMsg.crc));

		scrc = (short) (((hMsg.crc[0] & 0xFF) << 8) | (hMsg.crc[1] & 0xFF));

		if (R2000_calibration.calcCrc_short(hMsg.getcheckcrcdata()) != scrc) {
			/* SLOS_Sleep(1500); */
			if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
				return READER_ERR.MT_IO_ERR.value();
			} else {
				return COMM_NON_FATAL_ERR;
			}
		}

		if (err != READER_ERR.MT_OK_ERR) {
			if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR)
				return READER_ERR.MT_IO_ERR.value();
		}
		return 0;

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carrywavelayout);
		Application app = getApplication();
		myapp = (MyApplication) app;
		ant=myapp.Rparams.opant;
		tv_ibpow = (TextView) findViewById(R.id.textView_invenbfp);
		tv_ibrpow = (TextView) findViewById(R.id.textView_invenbrp);
		tv_ipow = (TextView) findViewById(R.id.textView_invenfp);
		tv_iapow = (TextView) findViewById(R.id.textView_invenafp);
		tv_iarpow = (TextView) findViewById(R.id.textView_invenarp);
		tv_cnt = (TextView) findViewById(R.id.textView_invencnt);
		tv_tep = (TextView) findViewById(R.id.textView_inventep);

		drawview = (myview) this.findViewById(R.id.myview);

		button_on = (Button) findViewById(R.id.button_on);
		button_off = (Button) findViewById(R.id.button_off);
		button_off.setEnabled(false);
		button_getafpow = (Button) findViewById(R.id.button_getafpow);

		button_cwset = (Button) findViewById(R.id.button_cwset);
		button_cwset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				try {
					ant = Integer
							.parseInt(((EditText) findViewById(R.id.editText_cwant))
									.getText().toString());
					power = Integer
							.parseInt(((EditText) findViewById(R.id.editText_cwpow))
									.getText().toString());
					fre = Integer
							.parseInt(((EditText) findViewById(R.id.editText__cwfre))
									.getText().toString());
				} catch (Exception ex) {
					Toast.makeText(SubCarryWaveActivity.this,
							MyApplication.Constr_excep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

				Toast.makeText(SubCarryWaveActivity.this,
						MyApplication.Constr_ok, Toast.LENGTH_SHORT).show();
			}

		});

		button_on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				try {
//开启还是关闭(1字节)+天线id(1字节)+功率(2字节)+频率(4字节)
					Reader.CustomParam_ST cpst=myapp.Mreader.new CustomParam_ST();
					cpst.ParamName="0";
					byte[] vals=new byte[9];
					int p=0;
					vals[p++]=0x01;
					vals[p++]=0x01;
					vals[p++]=(byte)ant;
					vals[p++]=(byte)((power&0xff00)>>8);
					vals[p++]=(byte)(power&0x00ff);
					vals[p++]=(byte)((fre&0xff000000)>>24);
					vals[p++]=(byte)((fre&0x00ff0000)>>16);
					vals[p++]=(byte)((fre&0x0000ff00)>>8);
					vals[p++]=(byte)(fre&0x000000ff);
					cpst.ParamVal=vals;
					myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_CUSTOM,cpst);

					button_on.setEnabled(false);
					button_off.setEnabled(true);
				} catch (Exception ex) {
					Toast.makeText(SubCarryWaveActivity.this,
							MyApplication.Constr_excep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		button_off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					//开启还是关闭(1字节)+天线id(1字节)+功率(2字节)+频率(4字节)
					Reader.CustomParam_ST cpst=myapp.Mreader.new CustomParam_ST();
					cpst.ParamName="0";
					byte[] vals=new byte[9];
					int p=0;
					vals[p++]=0x01;
					vals[p++]=0x00;
					vals[p++]=(byte)ant;
					vals[p++]=(byte)((power&0xff00)>>8);
					vals[p++]=(byte)(power&0x00ff);
					vals[p++]=(byte)((fre&0xff000000)>>24);
					vals[p++]=(byte)((fre&0x00ff0000)>>16);
					vals[p++]=(byte)((fre&0x0000ff00)>>8);
					vals[p++]=(byte)(fre&0x000000ff);
					cpst.ParamVal=vals;
					myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_CUSTOM,cpst);

					button_on.setEnabled(true);
					button_off.setEnabled(false);
				} catch (Exception ex) {
					Toast.makeText(SubCarryWaveActivity.this,
							MyApplication.Constr_excep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		button_getafpow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {

					ReadA0A1A2();
					R2000Tdata r2000td = new R2000Tdata();
					double[] ads = new double[5];

					Readreflect_diff(fre);
					r2000td.refpower1 = ReadFPower() * 0.1;
					ads[0] = (r2000td.refpower1 - ra0) / ra1;
					r2000td.power1 = ReadPower() * 0.1;
					ads[1] = (r2000td.power1 - fa0) / fa1;
					tv_ibpow.setText(MyApplication.Constr_carry_binvpw
							+ String.format("%.1f", r2000td.power1) + "("
							+ String.format("%.1f", ads[1]) + ")");
					tv_ibrpow.setText(MyApplication.Constr_carry_binvfpw
							+ String.format("%.1f", r2000td.refpower1) + "("
							+ String.format("%.1f", ads[0]) + ")");

					int[] tagcnt = new int[1];
					tagcnt[0] = 0;
					er = myapp.Mreader.TagInventory_Raw(myapp.Rparams.uants,
							myapp.Rparams.uants.length,
							(short) myapp.Rparams.readtime, tagcnt);

					for (int i = 0; i < tagcnt[0]; i++) {
						TAGINFO tfs = myapp.Mreader.new TAGINFO();
						myapp.Mreader.GetNextTag(tfs);
					}
					tv_cnt.setText(MyApplication.Constr_carry_invc
							+ String.valueOf(tagcnt[0]));

					int[] temval = new int[1];
					myapp.Mreader.ParamGet(Mtr_Param.MTR_PARAM_RF_TEMPERATURE,
							temval);
					tv_tep.setText(MyApplication.Constr_carry_invtep
							+ String.valueOf(temval[0]) + "°C");

					r2000td.power2 = ReadPower() * 0.1;
					ads[2] = (r2000td.power2 - fa0) / fa1;
					tv_ipow.setText(MyApplication.Constr_carry_invpw
							+ String.format("%.1f", r2000td.power2) + "("
							+ String.format("%.1f", ads[2]) + ")");

					Readreflect_diff(fre);
					r2000td.refpower3 = ReadFPower() * 0.1;
					ads[3] = (r2000td.refpower3 - ra0) / ra1;
					tv_iapow.setText(MyApplication.Constr_carry_ainvfpw
							+ String.format("%.1f", r2000td.refpower3) + "("
							+ String.format("%.1f", ads[3]) + ")");
					r2000td.power3 = ReadPower() * 0.1;
					ads[4] = (r2000td.power3 - fa0) / fa1;
					tv_iarpow.setText(MyApplication.Constr_carry_ainvpw
							+ String.format("%.1f", r2000td.power3) + "("
							+ String.format("%.1f", ads[4]) + ")");

					Toast.makeText(SubCarryWaveActivity.this,
							MyApplication.Constr_ok, Toast.LENGTH_SHORT).show();
				} catch (Exception ex) {
					Toast.makeText(SubCarryWaveActivity.this,
							MyApplication.Constr_excep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		button_cabwgraph = (Button) findViewById(R.id.button_cabwtest);
		button_cabwgraph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				frequency_FAD();
			}

		});
		button_cabwgraph2 = (Button) findViewById(R.id.button_rewtest);
		button_cabwgraph2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				frequency_Returnloss();
			}

		});
		
		button_returnloss = (Button) findViewById(R.id.button_freLosslist);
		button_returnloss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				frequency_Returnloss2();
			}

		});
		
		button_allfreturnloss=(Button) findViewById(R.id.button_api_getRL);
		
		button_allfreturnloss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				try {
					ant = Integer
							.parseInt(((EditText) findViewById(R.id.editText_cwant))
									.getText().toString());
					power = Integer
							.parseInt(((EditText) findViewById(R.id.editText_cwpow))
									.getText().toString());
					 
				} catch (Exception ex) {
					
					Toast.makeText(SubCarryWaveActivity.this,
							MyApplication.Constr_excep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
				frequency_Returnloss3();
			}

		});
	}
    //频率-反向功率值
	private void frequency_Returnloss() {

		try {
			drawview.drawClear();

			htbindex = 0;

			HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
			READER_ERR er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);

			if (er == READER_ERR.MT_OK_ERR) {

				tablefre = commfun.Sort(hdst2.htb, hdst2.lenhtb);

			}

			ReadA0A1A2();
			setantpow(ant, power);
			Lires.clear();
			String qiex = "";
			for (int i = 0; i < tablefre.length; i++) {

				setpll(tablefre[htbindex++]);
				CW_On();
				Readreflect_diff(fre);

				Lires.add(new BigDecimal(String.valueOf(ReadFPower() * 0.1
						- (power / 100 - 10)))
						.setScale(1, RoundingMode.HALF_UP).toString());

				CW_Off();

			}
			if (Lires.get(0).contains("-"))
				qiex = "-";

			List<String> AlFre = new ArrayList<String>();
			for (int i = 0, j = 0; i < myapp.allhtb.length; i++) {
				AlFre.add(String.valueOf(myapp.allhtb[i]));
			}

			List<String> LiFre = new ArrayList<String>();
			for (int i = 0; i < tablefre.length; i++) {
				LiFre.add(String.valueOf(tablefre[i]));
			}
			// draw
			String[] xstr = new String[myapp.allhtb.length];
			String[] pstr = new String[myapp.allhtb.length];
			List<Integer> xval = new ArrayList<Integer>();
			List<Integer> yval = new ArrayList<Integer>();

			List<Float> lystr = new ArrayList<Float>();
			lystr.add((float) 0);
			for (int i = 0, j = 0; i < myapp.allhtb.length; i++) {

				xstr[i] = String.valueOf(myapp.allhtb[i]);

				if (LiFre.contains(AlFre.get(i))) {
					if (lystr
							.contains(Math.abs(Float.parseFloat(Lires.get(j)))))
						j++;
					else
						lystr.add(Math.abs(Float.parseFloat(Lires.get(j++))));
				}
				xval.add(i + 1);
			}

			Collections.sort(lystr);
			for (int i = 0, j = 0; i < AlFre.size(); i++) {
				if (LiFre.contains(AlFre.get(i))) {

					float f1 = (Float.parseFloat(Lires.get(j)));
					if (f1 == 0)
						yval.add(0);
					else {
						int t1 = (int) (Height * 1.0
								/ lystr.get(lystr.size() - 1) * (Math.abs(Float
								.parseFloat(Lires.get(j)))));
						yval.add(t1);
					}
					pstr[i] = Lires.get(j++);// +"(-" +
												// TF.Lv.Items[i].SubItems[1].Text
												// + ")";
				} else {
					yval.add(0);
					pstr[i] = "";
				}
			}

			List<String> ls = new ArrayList<String>();
			for (int i = 0; i < spit + 1; i++) {
				float flt = lystr.get(lystr.size() - 1);
				ls.add(qiex
						+ String.valueOf(new BigDecimal((float) (flt * 1.0
								/ spit * i)).setScale(2, RoundingMode.HALF_UP)
								.floatValue()));
			}
			String[] ls2 = new String[ls.size()];
			ls.toArray(ls2);
			Integer[] xval2 = new Integer[xval.size()];
			xval.toArray(xval2);

			Integer[] yval2 = new Integer[yval.size()];
			yval.toArray(yval2);
			DrawXY_fre_power(false, MyApplication.Constr_carry_frtable, Height,
					Width, xstr, ls2, pstr, xval2, yval2);
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	public float Findthenearnum(float flval,Map Dii)
    {
        float key = 0, val = 0;
        float uflval=Math.abs(flval-0);
        Iterator<Entry<Float, Float>> iesb;
		iesb =Dii.entrySet().iterator();
		
		while (iesb.hasNext()) {
		 
			Entry<Float, Float> eff=iesb.next();
			Float curkey = eff.getKey();
			Float curval = eff.getValue();
			
			if (key == 0 || Math.abs(curval - uflval) < val)
            {
                key = curkey;
                val = Math.abs(curval - uflval);
            }
			 
		}
		 
        return key;
    }
	private void frequency_Returnloss2() {
		try{
		Map<Float, Float> Dii = new LinkedHashMap<Float, Float>();
		 Dii.put(1.00f, -1f);
         Dii.put(1.01f, 46.1f);
         Dii.put(1.02f, 40.1f);
         Dii.put(1.03f, 36.6f);
         Dii.put(1.04f, 34.2f);

         Dii.put(1.05f, 32.3f);
         Dii.put(1.06f, 30.7f);
         Dii.put(1.07f, 29.4f);
         Dii.put(1.08f, 28.3f);
         Dii.put(1.09f, 27.3f);

         Dii.put(1.10f, 26.4f);
         Dii.put(1.11f, 25.7f);
         Dii.put(1.12f, 24.9f);
         Dii.put(1.13f, 24.3f);
         Dii.put(1.14f, 23.7f);

         Dii.put(1.15f, 23.1f);
         Dii.put(1.16f, 22.6f);
         Dii.put(1.17f, 22.1f);
         Dii.put(1.18f, 21.7f);
         Dii.put(1.19f, 21.2f);

         Dii.put(1.20f, 20.8f);
         Dii.put(1.21f, 20.4f);
         Dii.put(1.22f, 20.1f);
         Dii.put(1.23f, 19.7f);
         Dii.put(1.24f, 19.4f);

         Dii.put(1.25f, 19.1f);
         Dii.put(1.26f, 18.8f);
         Dii.put(1.27f, 18.5f);
         Dii.put(1.28f, 18.2f);
         Dii.put(1.29f, 17.9f);

         Dii.put(1.30f, 17.7f);
         Dii.put(1.32f, 17.2f);
         Dii.put(1.34f, 16.8f);
         Dii.put(1.36f, 16.3f);
         Dii.put(1.38f, 15.9f);

         Dii.put(1.40f, 15.6f);
         Dii.put(1.42f, 15.2f);
         Dii.put(1.44f, 14.9f);
         Dii.put(1.46f, 14.6f);
         Dii.put(1.48f, 14.3f);

         Dii.put(1.50f, 14.0f);
         Dii.put(1.52f, 13.7f);
         Dii.put(1.54f, 13.4f);
         Dii.put(1.56f, 13.2f);
         Dii.put(1.58f, 13.0f);

         Dii.put(1.60f, 12.7f);
         Dii.put(1.62f, 12.5f);
         Dii.put(1.64f, 12.3f);
         Dii.put(1.66f, 12.1f);
         Dii.put(1.68f, 11.9f);

         Dii.put(1.70f, 11.7f);
         Dii.put(1.72f, 11.5f);
         Dii.put(1.74f, 11.4f);
         Dii.put(1.76f, 11.2f);
         Dii.put(1.78f, 11.0f);

         Dii.put(1.80f, 10.9f);
         Dii.put(1.82f, 10.7f);
         Dii.put(1.84f, 10.5f);
         Dii.put(1.86f, 10.4f);
         Dii.put(1.88f, 10.3f);

         Dii.put(1.90f, 10.2f);
         Dii.put(1.92f, 10.0f);
         Dii.put(1.94f, 9.9f);
         Dii.put(1.96f, 9.8f);
         Dii.put(1.98f, 9.7f);

         Dii.put(2.00f, 9.5f);
         Dii.put(2.50f, 7.4f);
         Dii.put(3.00f, 6.0f);
         Dii.put(3.50f, 5.1f);
         Dii.put(4.00f, 4.4f);

         Dii.put(4.50f, 3.9f);
         Dii.put(5.00f, 3.5f);
         Dii.put(5.50f, 3.2f);
         Dii.put(6.00f, 2.9f);
         Dii.put(6.50f, 2.7f);

         Dii.put(7.00f, 2.5f);
         Dii.put(7.50f, 2.3f);
         Dii.put(8.00f, 2.2f);
         Dii.put(8.50f, 2.1f);
         Dii.put(9.00f, 1.9f);


         Dii.put(9.50f, 1.8f);
         Dii.put(10.00f, 1.7f);
         Dii.put(11.00f, 1.6f);
         Dii.put(12.00f, 1.5f);
         Dii.put(13.00f, 1.3f);

         Dii.put(14.00f, 1.2f);
         Dii.put(15.00f, 1.2f);
         Dii.put(16.00f, 1.1f);
         Dii.put(17.00f, 1.0f);
         Dii.put(18.00f, 1.0f);

         Dii.put(19.00f, 0.9f);
         Dii.put(20.00f, 0.9f);
         Dii.put(25.00f, 0.7f);
         Dii.put(30.00f, 0.6f);
         Dii.put(35.00f, 0.5f);


         Dii.put(40.00f, 0.4f);
         Dii.put(45.00f, 0.4f);
         Dii.put(50.00f, 0.3f);
         Dii.put(55.00f, 0.3f);
         Dii.put(60.00f, 0.3f);

         Dii.put(65.00f, 0.3f);
         Dii.put(70.00f, 0.2f);
         Dii.put(80.00f, 0.2f);
         Dii.put(90.00f, 0.2f);
         Dii.put(100.00f, 0.2f);
		
		htbindex = 0;

		HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
		READER_ERR er = myapp.Mreader.ParamGet(
				Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);

		if (er == READER_ERR.MT_OK_ERR) 
			tablefre = commfun.Sort(hdst2.htb, hdst2.lenhtb);
		else
		{
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + er.toString(),
					Toast.LENGTH_SHORT).show();
			return;
		}

		String msg="频率              RL(dB)          VSWR          天线\r\n";
		TextView tv=(TextView)this.findViewById(R.id.textView_frelostlist);
		
		  List<String[]> ls=new ArrayList<String[]>();
		for (int j = 0; j < tablefre.length; j++) {

		VSWRReturnloss_DATA et2=Readreflect_diff2(new int[]{tablefre[j]});
		
		if(et2!=null)
		{
			
			for(int i=0;i<et2.Lires().size();i++)
			{	
				 float ft1=(new BigDecimal((float) (et2.Lires().get(0)&0xff) / 10)).pow(1).floatValue();
			     float ft2=Findthenearnum((new BigDecimal((float) (et2.Lires().get(0)&0xff) / 10)).pow(1).floatValue(),Dii);
			     msg+=String.valueOf(tablefre[j])+"             -"+String.valueOf(ft1)+"          "+
			     String.valueOf(ft2)+"               "+String.valueOf(ant)+"\r\n";
			     String[] ss=new String[4];
			     ss[0]=String.valueOf(tablefre[j]);
			     ss[1]=String.valueOf(ft1);
			     ss[2]=String.valueOf(ft2);
			     ss[3]=String.valueOf(ant);
			     ls.add(ss);
			 }
		}
		}
		
		 DjxlExcel dexel=new DjxlExcel("驻波报表");
		 dexel.CreatereturnlossExcelfile(ls);
		tv.setText(msg);
 
		}catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}
	private void frequency_Returnloss3() {
		try{

			AntPortsVSWR apvr=myapp.Mreader.new AntPortsVSWR();
			apvr.andid=ant;
			apvr.power=(short) myapp.Rparams.rpow[0];
			Region_Conf[] rcf2 = new Region_Conf[1];
			er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
			if (er == READER_ERR.MT_OK_ERR)
				apvr.region=rcf2[0];

			HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
			er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
			apvr.frecount=hdst2.lenhtb;
			for(int i=0;i<apvr.frecount;i++)
			{
				apvr.vswrs[i].frequency=hdst2.htb[i];
			}

			er=myapp.Mreader.ParamGet(Mtr_Param.MTR_PARAM_RF_ANTPORTS_VSWR, apvr);

			htbindex = 0;

		if (er == READER_ERR.MT_OK_ERR) 
			tablefre = commfun.Sort(hdst2.htb, hdst2.lenhtb);
		else
		{
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + er.toString(),
					Toast.LENGTH_SHORT).show();
			return;
		}

		String msg="频率              RL(dB)          VSWR          天线\r\n";
		TextView tv=(TextView)this.findViewById(R.id.textView_frelostlist);
		
		  List<String[]> ls=new ArrayList<String[]>();
		 
			
			for(int j=0;j<apvr.frecount;j++)
			{	
				 float ft1=0;
			     float ft2=apvr.vswrs[j].vswr;
			     msg+=String.valueOf(tablefre[j])+"             -"+String.valueOf(ft1)+"          "+
			     String.valueOf(ft2)+"               "+String.valueOf(ant)+"\r\n";
			     
			     String[] ss=new String[4];
			     ss[0]=String.valueOf(tablefre[j]);
			     ss[1]=String.valueOf(ft1);
			     ss[2]=String.valueOf(ft2);
			     ss[3]=String.valueOf(ant);
			     ls.add(ss);
			 }
		 
		
		 DjxlExcel dexel=new DjxlExcel("驻波报表");
		 dexel.CreatereturnlossExcelfile(ls);
		tv.setText(msg);
 
		}catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}
     //频率-反向功率值的AD值
	private void frequency_FAD() {

		try {
			drawview.drawClear();

			// drawview.drawCodi();
			// if(!istesting)
			// {

			// *
			htbindex = 0;

			HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
			READER_ERR er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);

			if (er == READER_ERR.MT_OK_ERR) {

				tablefre = commfun.Sort(hdst2.htb, hdst2.lenhtb);

			}

			ReadA0A1A2();
			setantpow(ant, power);
			Lires.clear();
			for (int i = 0; i < tablefre.length; i++) {
				setpll(tablefre[htbindex++]);
				CW_On();
				Lires.add(String.valueOf(ReadFAD()));//反向AD值
				CW_Off();
			}
			List<String> AlFre = new ArrayList<String>();
			for (int i = 0, j = 0; i < myapp.allhtb.length; i++) {
				AlFre.add(String.valueOf(myapp.allhtb[i]));
			}

			List<String> LiFre = new ArrayList<String>();
			for (int i = 0; i < tablefre.length; i++) {
				LiFre.add(String.valueOf(tablefre[i]));
			}
			// draw
			String[] xstr = new String[myapp.allhtb.length];
			String[] pstr = new String[myapp.allhtb.length];
			List<Integer> xval = new ArrayList<Integer>();
			List<Integer> yval = new ArrayList<Integer>();

			List<Float> lystr = new ArrayList<Float>();
			lystr.add((float) 0);
			for (int i = 0, j = 0; i < myapp.allhtb.length; i++) {

				xstr[i] = String.valueOf(myapp.allhtb[i]);

				if (LiFre.contains(AlFre.get(i))) {
					if (lystr.contains(Float.parseFloat(Lires.get(j))))
						j++;
					else
						lystr.add(Float.parseFloat(Lires.get(j++)));
				}
				xval.add(i + 1);
			}

			Collections.sort(lystr);
			for (int i = 0, j = 0; i < AlFre.size(); i++) {
				if (LiFre.contains(AlFre.get(i))) {

					float f1 = (Float.parseFloat(Lires.get(j)));
					if (f1 == 0)
						yval.add(0);
					else
						yval.add((int) (Height * 1.0
								/ lystr.get(lystr.size() - 1) * (Float
								.parseFloat(Lires.get(j)))));

					pstr[i] = Lires.get(j++);// +"(-" +
												// TF.Lv.Items[i].SubItems[1].Text
												// + ")";
				} else {
					yval.add(0);
					pstr[i] = "";
				}
			}

			List<String> ls = new ArrayList<String>();
			for (int i = 0; i < spit + 1; i++) {

				ls.add(String.valueOf(new BigDecimal((float) (lystr.get(lystr
						.size() - 1) * 1.0 / spit * i)).setScale(2,
						RoundingMode.HALF_UP).floatValue()));
			}
			String[] ls2 = new String[ls.size()];
			ls.toArray(ls2);
			Integer[] xval2 = new Integer[xval.size()];
			xval.toArray(xval2);

			Integer[] yval2 = new Integer[yval.size()];
			yval.toArray(yval2);
			DrawXY_fre_power(true, MyApplication.Constr_carry_fftable, Height,
					Width, xstr, ls2, pstr, xval2, yval2);

		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}

		/*
		 * handler.postDelayed(runnable_2, 0);
		 * button_cabwgraph.setText(myapp.Constr_stop); istesting=true;
		 * 
		 * } else { handler.removeCallbacks(runnable_2);
		 * button_cabwgraph.setText(getString(R.string.action_cw));
		 * istesting=false;
		 * 
		 * }
		 */
	}

	private void DrawXY_fre_power(boolean bl, String title, int h, int w,
			String[] xstr, String[] ystr, String[] pstr, Integer[] xval,
			Integer[] yval) throws Exception {

		try {

			// 中心轴
			int x = x_st;
			int y = h + y_st;

			drawview.drawText(150, 150, 35, title);

			// 绘制纵向线条
			drawview.drawLine(x, y_st, x, y);

			int stx1 = 0, sty1 = 0, stx2, sty2;

			for (int i = 0; i < xval.length; i++) {
				stx2 = xval[i] * x_w + x_wadd;
				sty2 = y - yval[i];
				// g.FillEllipse(brush2, stx2, sty2, 3, 3);

				if (i > 0) {
					if (!pstr[i].isEmpty()) {
						if (stx1 != 0 && sty1 != 0) {
							drawview.drawLine(stx1, sty1, stx2, sty2);
						}
						if (bl == false) {
							drawview.drawText(stx2 - xy_x_char, sty2
									- xy_y_char, 5, pstr[i] + "\r\n");
						} else
							drawview.drawText(
									stx2 - xy_x_char,
									sty2 - xy_y_char,
									5,
									pstr[i]
											+ "\r\n("
											+ new BigDecimal(
													(Double.parseDouble(pstr[i]) * ra1)
															+ ra0).setScale(1,
													RoundingMode.HALF_UP)
													.floatValue() + ")");//反向AD值或者反向功率值（该值*A1+A0）
						stx1 = stx2;
						sty1 = sty2;
					}

				} else {
					if (!pstr[i].isEmpty()) {
						stx1 = xval[i] * x_w + x_wadd;
						sty1 = y - yval[i];
						drawview.drawText(stx1 - xy_x_char, sty1 - xy_y_char,
								5, pstr[i]);
					}
				}
				// 绘制xy坐标竖线条
				if (!pstr[i].isEmpty())
					drawview.drawLine(stx1, sty1, stx1, y);
			}

			// 绘制横向线条
			drawview.drawLine(x_st, y, w, y);

			// x轴上对应的标记
			x = x_st;
			for (int i = 0; i < xstr.length; i++) {
				drawview.drawText(x, y + x_char, 5, xstr[i]);
				// 设置文字内容及输出位置
				x = x + x_w;
			}
			// y轴上对应的标记
			// *
			for (int i = 0; i < ystr.length; i++) {
				drawview.drawText(x_st - 40, y - h / (ystr.length - 1) * i, 5,
						ystr[i]); // 设置文字内容及输出位置
				int tempy = y - h / (ystr.length - 1) * i;
				drawview.drawLine(x_st, tempy, x_st + x_wbl, tempy);
				if (i < ystr.length - 1) {
					drawview.drawLine(x_st, tempy - h / (ystr.length - 1)
							/ spit, x_st + x_wsl, tempy - h / (ystr.length - 1)
							/ spit);
					drawview.drawLine(x_st, tempy - h / (ystr.length - 1)
							/ spit * 2, x_st + x_wsl, tempy - h
							/ (ystr.length - 1) / spit * 2);
					drawview.drawLine(x_st, tempy - h / (ystr.length - 1)
							/ spit * 3, x_st + x_wsl, tempy - h
							/ (ystr.length - 1) / spit * 3);
					drawview.drawLine(x_st, tempy - h / (ystr.length - 1)
							/ spit * 4, x_st + x_wsl, tempy - h
							/ (ystr.length - 1) / spit * 4);

				}
			}

			// */
		} catch (Exception ex) {
			throw ex;
		}

	}

	int[] tablefre;
	int htbindex = 0;
	List<String> Lires = new ArrayList<String>();
	private Handler handler = new Handler();
	private Runnable runnable_2 = new Runnable() {
		public void run() {

			try {
				setpll(tablefre[htbindex]);
				CW_On();

				Lires.add(String.valueOf(ReadFAD()));

				CW_Off();

				// draw graphics

			} catch (Exception ex) {
				return;
			}
		}
	};

	// 获取前向-反向差值
	private double Readreflect_diff(int frequency) {
		try {
			Region_Conf[] rcf2 = new Region_Conf[1];
			READER_ERR er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
			Region rg = Region.NA;

			switch (rcf2[0]) {
			case RG_PRC:
				rg = Region.PRC;
				break;
			case RG_EU:
				rg = Region.EU;
				break;
			case RG_EU2:
				rg = Region.EU2;
				break;
			case RG_EU3:
				rg = Region.EU3;
				break;
			case RG_KR:
				rg = Region.KR;
				break;
			case RG_NA:
				rg = Region.NA;
				break;
			default:
				rg = Region.NA;
				break;
			}

			R2000_calibration.VSWRReturnloss_DATA et = new R2000_calibration().new VSWRReturnloss_DATA(
					power,  new int[]{frequency}, new int[] { ant }, rg);
			byte[] senddata = r2000pcmd.GetSendCmd(
					R2000_calibration.R2000cmd.ReturnLossTest, et.ToByteData());
			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1500, hMsg);
			if (re != 0) {
				throw new Exception("no resp");
			}
			if (hMsg.status[0] + hMsg.status[1] != 0) {
				throw new Exception(myapp.Mreader.bytes_Hexstr(hMsg.status));
			}
			if (hMsg.opCode[0] == 0) {
				Toast.makeText(SubCarryWaveActivity.this,
						MyApplication.Constr_excep, Toast.LENGTH_SHORT).show();
			}

			byte[] data = new byte[hMsg.dataLen[0] - 12];
			System.arraycopy(hMsg.data, 12, data, 0, data.length);
			R2000_calibration.VSWRReturnloss_DATA et2 = new R2000_calibration().new VSWRReturnloss_DATA(
					data);
			// return Math.Round((float)(et2.Lires().get(0)) / 10, 1);
			return (new BigDecimal((float) (et2.Lires().get(0)) / 10)).pow(1)
					.intValue();
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return 0;
	}
	private VSWRReturnloss_DATA Readreflect_diff2(int[] frequency) {
		try {
			Region_Conf[] rcf2 = new Region_Conf[1];
			READER_ERR er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
		 	Region rg = Region.NA;
            if(rcf2==null)
            	return null;
			switch (rcf2[0]) {
			case RG_PRC:
				rg = Region.PRC;
				break;
			case RG_EU:
				rg = Region.EU;
				break;
			case RG_EU2:
				rg = Region.EU2;
				break;
			case RG_EU3:
				rg = Region.EU3;
				break;
			case RG_KR:
				rg = Region.KR;
				break;
			case RG_NA:
				rg = Region.NA;
				break;
			default:
				rg = Region.NA;
				break;
			}

			R2000_calibration.VSWRReturnloss_DATA et = new R2000_calibration().new VSWRReturnloss_DATA(
					power,  frequency, new int[] { ant }, rg);
			byte[] senddata = r2000pcmd.GetSendCmd(
					R2000_calibration.R2000cmd.ReturnLossTest, et.ToByteData());
			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 5000, hMsg);
			if (re != 0) {
				throw new Exception("no resp");
			}
			if (hMsg.status[0] + hMsg.status[1] != 0) {
				throw new Exception(myapp.Mreader.bytes_Hexstr(hMsg.status));
			}
			if (hMsg.opCode[0] == 0) {
				Toast.makeText(SubCarryWaveActivity.this,
						MyApplication.Constr_excep, Toast.LENGTH_SHORT).show();
			}

			byte[] data = new byte[hMsg.dataLen[0] - 12];
			System.arraycopy(hMsg.data, 12, data, 0, data.length);
			R2000_calibration.VSWRReturnloss_DATA et2 = new R2000_calibration().new VSWRReturnloss_DATA(
					data);
			
			return et2;
			
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	// 获取计算前向反向功率的 A0，A1，A2值
	private void ReadA0A1A2() throws Exception {
		try {

			// *
			short[] oemaddr = new short[6];
			oemaddr[0] = oemcfgaddr_rfcal_fwdpwr_A2;
			oemaddr[1] = oemcfgaddr_rfcal_fwdpwr_A1;
			oemaddr[2] = oemcfgaddr_rfcal_fwdpwr_A0;
			oemaddr[3] = oemcfgaddr_rfcal_revpwr_A2;
			oemaddr[4] = oemcfgaddr_rfcal_revpwr_A1;
			oemaddr[5] = oemcfgaddr_rfcal_revpwr_A0;

			R2000_calibration.OEM_DATA r2000oem = new R2000_calibration().new OEM_DATA(
					oemaddr[0]);
			for (int i = 1; i < 6; i++) {
				r2000oem.AddTo(oemaddr[i], 0);
			}
			byte[] senddata = null;
			senddata = r2000pcmd.GetSendCmd(rcmdo2, r2000oem.ToByteData());
			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1000, hMsg);
			if (re != 0 || hMsg.opCode[0] == 0) {
				throw new Exception("no any data resp");
			}

			byte[] data = new byte[hMsg.dataLen[0] - 12];
			System.arraycopy(hMsg.data, 12, data, 0, data.length);
			R2000_calibration.OEM_DATA r2000data = new R2000_calibration().new OEM_DATA(
					data);

			R2000_calibration.OEM_DATA.Adpair[] adp = r2000data.GetAddr();
			// String val1= String.format("%02X",adp[2].val);

			fa2 = Double.parseDouble(HexstrToQ724(adp[0].val));
			fa1 = Double.parseDouble(HexstrToQ724(adp[1].val));
			fa0 = Double.parseDouble(HexstrToQ724(adp[2].val));

			ra2 = Double.parseDouble(HexstrToQ724(adp[3].val));
			ra1 = Double.parseDouble(HexstrToQ724(adp[4].val));
			ra0 = Double.parseDouble(HexstrToQ724(adp[5].val));
		} catch (Exception ex) {

			throw ex;
		}

	}

	// 获取前向功率值，即发射功率值,单位0.1 dbm

	private int ReadPower() {

		try {
			byte[] senddata = null;

			senddata = r2000pcmd.GetSendCmd(rcmdo, r2000macp.ToByteData());
			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1000, hMsg);
			if (re != 0) {
				throw new Exception("no resp");
			}
			if (hMsg.opCode[0] == 0) {
				Toast.makeText(SubCarryWaveActivity.this,
						MyApplication.Constr_excep, Toast.LENGTH_SHORT).show();
			}

			byte[] data = new byte[hMsg.dataLen[0] - 12];
			System.arraycopy(hMsg.data, 12, data, 0, data.length);

			R2000_calibration.MAC_DATA r2000data = new R2000_calibration().new MAC_DATA(
					data);
			R2000_calibration.MAC_DATA.Adpair[] adp = r2000data.GetAddr();

			return adp[0].val;
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return 0;
		}
	}

	// 获取反向功率值，即发射功率值,单位0.1 dbm
	private int ReadFPower() {

		try {
			byte[] senddata = null;

			senddata = r2000pcmd.GetSendCmd(rcmdo, r2000macfp.ToByteData());

			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1000, hMsg);
			if (re != 0) {
				throw new Exception("no resp");
			}
			if (hMsg.opCode[0] == 0) {
				Toast.makeText(SubCarryWaveActivity.this,
						MyApplication.Constr_excep, Toast.LENGTH_SHORT).show();
			}

			byte[] data = new byte[hMsg.dataLen[0] - 12];
			System.arraycopy(hMsg.data, 12, data, 0, data.length);

			R2000_calibration.MAC_DATA r2000data = new R2000_calibration().new MAC_DATA(
					data);
			R2000_calibration.MAC_DATA.Adpair[] adp = r2000data.GetAddr();

			return adp[0].val;
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return 0;
		}
	}

	public String PadLeft(String strval, int len, char adtn) {
		return String.format("%" + len + "s" + adtn, strval);
	}

	public String PadRight(String strval, int len, char adtn) {
		return String.format("%-" + len + "s" + adtn, strval);
	}

	public String HexstrToQ724(int val) throws Exception {
		int valuepint = val;
		double value_q714 = valuepint * 1.0
				/ (new BigDecimal(2)).pow(24).intValue();// Math.Pow(2, 24);

		String q714 = String.valueOf(value_q714);
		if (value_q714 > 0)
			return String.valueOf(String.valueOf(q714).substring(0, 6));
		else
			return q714.length() >= 7 ? String.valueOf(q714).substring(0, 7)
					: q714;
	}

	public boolean CheckDatarule(String data, int numk) {
		boolean returnvalue = true;
		if (data.isEmpty() || data == null) {
			return false;
		}
		if (numk == 2) {
			for (int i = 0; i < data.length(); i++) {
				char single = data.charAt(i);
				if (single != '0' && single != '1')
					return false;
			}
		} else if (numk == 10) {

			for (int i = 0; i < data.length(); i++) {
				char single = data.charAt(i);
				switch (single) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					break;
				default: {
					returnvalue = false;
					break;
				}

				}
				if (!returnvalue) {
					break;
				}
			}
		} else {
			for (int i = 0; i < data.length(); i++) {
				char single = data.charAt(i);
				// String temp=String.valueOf(single).toUpperCase();
				switch (single) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
					break;
				default: {
					returnvalue = false;
					break;
				}
				}
				if (!returnvalue) {
					break;
				}
			}
		}

		if (data.substring(0, 1) == "."
				|| data.substring(data.length() - 1, data.length()) == ".")
			returnvalue = false;
		return returnvalue;
	}

	public void setantpow(int ant, int pow) {

		R2000_calibration.ENGTest_DATA et = new R2000_calibration().new ENGTest_DATA(
				(byte) (R2000_calibration.SubCmd.SetTestAntPow.Value()), ant,
				pow);
		byte[] senddata = r2000pcmd.GetSendCmd(
				R2000_calibration.R2000cmd.ENGTEST, et.ToByteData());

		int re = 0;
		MsgObj hMsg = new MsgObj();
		re = SendandRev(senddata, 1000, hMsg);

	}

	private void CW_On() {
		try {

			byte[] senddata = r2000pcmd.GetSendCmd(
					R2000_calibration.R2000cmd.carrier, new byte[] { 0x01 });
			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1000, hMsg);

		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void CW_Off() {
		try {
			byte[] senddata = r2000pcmd.GetSendCmd(
					R2000_calibration.R2000cmd.carrier, new byte[] { 0x00 });

			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1000, hMsg);
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}

	}

	public void setpll(int fre) throws Exception {
		if (fre == 0) {
			throw new Exception("no frequency");
		}

		if (fre < 840000 || fre > 960000)
			throw new Exception("840000-960000");

		List<Byte> lb = new ArrayList<Byte>();
		lb.add((byte) 0);
		lb.add((byte) 0);
		lb.add((byte) 0);
		lb.add((byte) 0);

		byte[] bdata = new byte[4];
		bdata[0] = (byte) ((fre & 0xff000000) >> 24);
		bdata[1] = (byte) ((fre & 0x00ff0000) >> 16);
		bdata[2] = (byte) ((fre & 0x0000ff00) >> 8);
		bdata[3] = (byte) (fre & 0x000000ff);

		for (int i = 0; i < 4; i++)
			lb.add(bdata[i]);

		Byte[] by = new Byte[lb.size()];
		byte[] by2 = new byte[lb.size()];
		lb.toArray(by);
		for (int i = 0; i < by2.length; i++)
			by2[i] = by[i];

		byte[] senddata = r2000pcmd.GetSendCmd(
				R2000_calibration.R2000cmd.SetTestFre, by2);

		int re = 0;
		MsgObj hMsg = new MsgObj();
		re = SendandRev(senddata, 1000, hMsg);
	}

	// 获取反向功率值的AD值
	private int ReadFAD() {

		try {
			R2000_calibration.ENGTest_DATA et = new R2000_calibration().new ENGTest_DATA(
					(byte) (R2000_calibration.SubCmd.ReadAD.Value()), 1, 1);
			byte[] senddata = r2000pcmd.GetSendCmd(
					R2000_calibration.R2000cmd.ENGTEST, et.ToByteData());

			int re = 0;
			MsgObj hMsg = new MsgObj();
			re = SendandRev(senddata, 1000, hMsg);
			// byte[] revdata = SendandRev(senddata, 1000);

			boolean islow = ((hMsg.data[12] & 0x80) >> 7) == 1 ? true : false;

			int result = (hMsg.data[12] & 0xff) << 24
					| (hMsg.data[13] & 0xff) << 16
					| (hMsg.data[14] & 0xff) << 8 | (hMsg.data[15] & 0xff);
			int adc = (hMsg.data[16] & 0xff) << 24
					| (hMsg.data[17] & 0xff) << 16
					| (hMsg.data[18] & 0xff) << 8 | (hMsg.data[19] & 0xff);

			return adc;
		} catch (Exception ex) {
			Toast.makeText(SubCarryWaveActivity.this,
					MyApplication.Constr_excep + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return 0;
		}
	}
}
