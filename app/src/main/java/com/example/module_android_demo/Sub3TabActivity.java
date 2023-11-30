package com.example.module_android_demo;

import java.util.Random;

import com.function.commfun;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.Lock_Obj;
import com.uhf.api.cls.Reader.Lock_Type;
import com.uhf.api.cls.Reader.Mtr_Param;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TagFilter_ST;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

/**
 * RFID 单天线操作页面 包括读标签内存，写标签内存和锁定标签
 */
public class Sub3TabActivity extends Activity {

	Button button_readop, button_writeop, button_writepc, button_lockop,
			button_kill,button_testwrite,button_optimeset;
	Spinner spinner_opbank, spinner_bankr, spinner_bankw, spinner_lockbank,
			spinner_locktype;
	TabHost tabHost_op;
	RadioGroup gr_opant, gr_match, gr_enablefil, gr_wdatatype;
	CheckBox cb_pwd;
	// Switch swt_fil;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter arradp_bank, arradp_fbank, arradp_lockbank,
			arradp_locktype;
	MyApplication myapp;
	TagFilter_ST g2tf = null;
	public static EditText EditText_sub3fildata, EditText_sub3wdata;

	private View createIndicatorView(Context context, TabHost tabHost,
			String title) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View tabIndicator = inflater.inflate(R.layout.tab_indicator_vertical,
				tabHost.getTabWidget(), false);
		final TextView titleView = (TextView) tabIndicator
				.findViewById(R.id.tv_indicator);
		titleView.setText(title);
		return tabIndicator;
	}

	/**
	 * 单天线操作必须指定天线
	 */
	private void SetOpant() {
		myapp.Rparams.opant = commfun.SortGroup(gr_opant) + 1;
	}

	
	private void ClearFiler(){
		g2tf = null;
		myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
	}
	/**
	 * 设置过滤数据
	 */
	private void SetFiler() {

		if (myapp.nxpu8 != 0) {
			Toast.makeText(Sub3TabActivity.this, "NXP U8 MODE is using",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (commfun.SortGroup(gr_enablefil) == 1) {
			EditText et = (EditText) findViewById(R.id.editText_opfilterdata);
			int ln = et.getText().toString().length();
			if (ln == 1 || ln % 2 == 1)
				ln++;
			byte[] fdata = new byte[ln / 2];
			myapp.Mreader.Str2Hex(et.getText().toString(), et.getText()
					.toString().length(), fdata);

			g2tf = myapp.Mreader.new TagFilter_ST();
			g2tf.fdata = fdata;
			g2tf.flen = et.getText().toString().length() * 4;
			int ma = commfun.SortGroup(gr_match);
			if (ma == 1)
				g2tf.isInvert = 1;
			else
				g2tf.isInvert = 0;

			g2tf.bank = spinner_opbank.getSelectedItemPosition() + 1;

			EditText etadr = (EditText) findViewById(R.id.editText_opfilsadr);
			g2tf.startaddr = Integer.valueOf(etadr.getText().toString());

			READER_ERR er = myapp.Mreader.ParamSet(
					Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
			if (er == READER_ERR.MT_OK_ERR)
				Toast.makeText(Sub3TabActivity.this,
						MyApplication.Constr_ok + er.toString(),
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(Sub3TabActivity.this,
						MyApplication.Constr_failed + er.toString(),
						Toast.LENGTH_SHORT).show();
		} else {
			g2tf = null;
			myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
		}
	}

	/**
	 * 设置密码
	 */
	private void SetPassword() {
		myapp.Rparams.password = "";
		if (cb_pwd.isChecked()) {
			EditText et = (EditText) findViewById(R.id.editText_password);
			myapp.Rparams.password = et.getText().toString();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3_tablelayout);
		Application app = getApplication();
		myapp = (MyApplication) app;

		// 获取TabHost对象
		// 得到TabActivity中的TabHost对象
		tabHost_op = (TabHost) findViewById(R.id.tabhost3);
		// 如果没有继承TabActivity时，通过该种方法加载启动tabHost
		tabHost_op.setup();
		tabHost_op.getTabWidget().setOrientation(LinearLayout.VERTICAL);
		// tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator("初始化",
		// getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab11));
		// tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator(createIndicatorView(this,
		// tabHost2, "1111"))
		// .setContent(R.id.tab11));

		tabHost_op.addTab(tabHost_op
				.newTabSpec("tab1")
				.setIndicator(
						createIndicatorView(this, tabHost_op,
								MyApplication.Constr_sub3readmem))
				.setContent(R.id.tab3_sub_read));
		tabHost_op.addTab(tabHost_op
				.newTabSpec("tab2")
				.setIndicator(
						createIndicatorView(this, tabHost_op,
								MyApplication.Constr_sub3writemem))
				.setContent(R.id.tab3_sub_write));
		tabHost_op.addTab(tabHost_op
				.newTabSpec("tab3")
				.setIndicator(
						createIndicatorView(this, tabHost_op,
								MyApplication.Constr_sub3lockkill))
				.setContent(R.id.tab3_sub_lockkill));

		TabWidget tw = tabHost_op.getTabWidget();
		tw.getChildAt(0).setBackgroundColor(Color.BLUE);

		spinner_bankr = (Spinner) findViewById(R.id.spinner_bankr);
		// View layout =
		// getLayoutInflater().inflate(R.layout.tab3_tablelayout_write, null);
		spinner_bankw = (Spinner) findViewById(R.id.spinner_bankw);
		spinner_opbank = (Spinner) findViewById(R.id.spinner_opfbank);
		arradp_bank = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spibank);
		arradp_bank
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arradp_fbank = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spifbank);
		arradp_fbank
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_bankr.setAdapter(arradp_bank);
		spinner_bankw.setAdapter(arradp_bank);
		spinner_opbank.setAdapter(arradp_fbank);

		spinner_lockbank = (Spinner) findViewById(R.id.spinner_lockbank);
		// 将可选内容与ArrayAdapter连接起来
		arradp_lockbank = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spilockbank);
		// 设置下拉列表的风格
		arradp_lockbank
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		spinner_lockbank.setAdapter(arradp_lockbank);

		spinner_locktype = (Spinner) findViewById(R.id.spinner_locktype);
		arradp_locktype = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spilocktype);
		arradp_locktype
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_locktype.setAdapter(arradp_locktype);

		button_readop = (Button) findViewById(R.id.button_read);
		button_writeop = (Button) findViewById(R.id.button_write);
		button_writepc = (Button) findViewById(R.id.button_wepc);
		button_lockop = (Button) findViewById(R.id.button_lock);
		button_kill = (Button) findViewById(R.id.button_kill);
		button_testwrite = (Button) findViewById(R.id.button_testwrite);
		button_optimeset= (Button) findViewById(R.id.button_optimeset);
		gr_opant = (RadioGroup) findViewById(R.id.radioGroup_opant);
		gr_match = (RadioGroup) findViewById(R.id.radioGroup_opmatch);
		gr_enablefil = (RadioGroup) findViewById(R.id.radioGroup_enableopfil);
		gr_wdatatype = (RadioGroup) findViewById(R.id.radioGroup_datatype);
		cb_pwd = (CheckBox) findViewById(R.id.checkBox_opacepwd);
		EditText_sub3fildata = (EditText) findViewById(R.id.editText_opfilterdata);
		EditText_sub3wdata = (EditText) findViewById(R.id.editText_dataw);

		spinner_opbank.setSelection(0);
		spinner_bankr.setSelection(1);
		spinner_bankw.setSelection(1);
		spinner_lockbank.setSelection(2);
		spinner_locktype.setSelection(1);
		gr_opant.check(gr_opant.getChildAt(myapp.Rparams.opant - 1).getId());

		/**
		 * 读操作，首先配置操作天线，过滤密码，过滤条件，然后读出数据根据选择的编码方式显示数据
		 */
		button_readop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {

					SetOpant();
					SetPassword();
					SetFiler();

					EditText etcount = (EditText) findViewById(R.id.editText_opcountr);
					EditText etadr = (EditText) findViewById(R.id.editText_startaddr);
					EditText etdr = (EditText) findViewById(R.id.editText_datar);
					// EditText
					// etdrw=(EditText)findViewById(R.id.editText_startaddrw);
					byte[] rdata = new byte[Integer.valueOf(etcount.getText()
							.toString()) * 2];

					byte[] rpaswd = new byte[4];
					if (!myapp.Rparams.password.equals("")) {
						myapp.Mreader.Str2Hex(myapp.Rparams.password,
								myapp.Rparams.password.length(), rpaswd);
					}
					int test = spinner_bankr.getSelectedItemPosition();
					int test1 = test;
					char  test11  = (char) test1;
					String test2 = etadr.getText().toString();
					String test3 = etcount.getText().toString();
					READER_ERR er = READER_ERR.MT_OK_ERR;
					int trycount = 1;
					do {
						er = myapp.Mreader.GetTagData(myapp.Rparams.opant,
								(char) spinner_bankr.getSelectedItemPosition(),
						       		Integer.valueOf(etadr.getText().toString()),
								Integer.valueOf(etcount.getText().toString()),
								rdata, rpaswd, (short) myapp.Rparams.optime);
                        if(er != READER_ERR.MT_OK_ERR)
                        	myapp.Mreader.GetLastDetailError(myapp.ei);
						trycount--;
						if (trycount < 1)
							break;
					} while (er != READER_ERR.MT_OK_ERR);

					if (er == READER_ERR.MT_OK_ERR) {
						String val = "";
						char[] out = null;
						if (commfun.SortGroup(gr_wdatatype) == 0) {
							out = new char[rdata.length * 2];
							myapp.Mreader.Hex2Str(rdata, rdata.length, out);
							val = String.valueOf(out);
						} else if (commfun.SortGroup(gr_wdatatype) == 1) {
							out = new char[rdata.length];
							for (int i = 0; i < rdata.length; i++)
								out[i] = (char) rdata[i];
							val = String.valueOf(out);
						} else if (commfun.SortGroup(gr_wdatatype) == 2) {
							val = new String(rdata, "gbk");
						}
						etdr.setText(val);
						EditText_sub3wdata.setText(val);
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_ok + er.toString(),
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_failed + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				finally{
					 ClearFiler();
				}

			}

		});

		/**
		 * 写操作，首先配置操作天线，过滤密码，过滤条件，然后以不同编码数据写入标签
		 */
		button_writeop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					SetOpant();
					SetPassword();
					SetFiler();

					// EditText
					// etcount=(EditText)findViewById(R.id.editText_opcountw);
					EditText etadr = (EditText) findViewById(R.id.editText_startaddrw);

					byte[] data = null;
					if (commfun.SortGroup(gr_wdatatype) == 0) {
						data = new byte[EditText_sub3wdata.getText().toString()
								.length() / 2];
						myapp.Mreader.Str2Hex(EditText_sub3wdata.getText()
								.toString(), EditText_sub3wdata.getText()
								.length(), data);
					} else if (commfun.SortGroup(gr_wdatatype) == 1) {
						String ascstr = EditText_sub3wdata.getText().toString();
						if (ascstr.length() % 2 != 0)
							ascstr += "0";
						data = ascstr.getBytes();

					} else if (commfun.SortGroup(gr_wdatatype) == 2) {
						String ascstr = EditText_sub3wdata.getText().toString();

						data = ascstr.getBytes("gbk");
					}

					byte[] rpaswd = new byte[4];
					if (!myapp.Rparams.password.equals("")) {
						myapp.Mreader.Str2Hex(myapp.Rparams.password,
								myapp.Rparams.password.length(), rpaswd);
					}

					READER_ERR er = READER_ERR.MT_OK_ERR;
					int trycount = 1;
					do {
						er = myapp.Mreader.WriteTagData(myapp.Rparams.opant,
								(char) spinner_bankw.getSelectedItemPosition(),
								Integer.valueOf(etadr.getText().toString()),
								data, data.length, rpaswd,
								(short) myapp.Rparams.optime);
						
						 if(er != READER_ERR.MT_OK_ERR)
	                        	myapp.Mreader.GetLastDetailError(myapp.ei);
						 
						trycount--;
						if (trycount < 1)
							break;
					} while (er != READER_ERR.MT_OK_ERR);

					if (er == READER_ERR.MT_OK_ERR) {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_ok + er.toString(),
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_failed + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				finally{
					 ClearFiler();
				}
			}

		});

		/**
		 * 写epc 专门用于初始化标签 epcid
		 */
		button_writepc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				try {
					SetOpant();
					SetPassword();
					SetFiler();

					// EditText
					// etadr=(EditText)findViewById(R.id.editText_startaddrw);

					byte[] data = null;
					if (commfun.SortGroup(gr_wdatatype) == 0) {
						data = new byte[EditText_sub3wdata.getText().toString()
								.length() / 2];
						myapp.Mreader.Str2Hex(EditText_sub3wdata.getText()
								.toString(), EditText_sub3wdata.getText()
								.length(), data);
					} else if (commfun.SortGroup(gr_wdatatype) == 1) {
						String ascstr = EditText_sub3wdata.getText().toString();
						if (ascstr.length() / 2 != 0)
							ascstr += "0";

						data = ascstr.getBytes();
					} else if (commfun.SortGroup(gr_wdatatype) == 2) {
						String ascstr = EditText_sub3wdata.getText().toString();

						data = ascstr.getBytes("gbk");
					}

					byte[] rpaswd = new byte[4];
					if (!myapp.Rparams.password.equals("")) {
						myapp.Mreader.Str2Hex(myapp.Rparams.password,
								myapp.Rparams.password.length(), rpaswd);
					}

					READER_ERR er = READER_ERR.MT_OK_ERR;
					int trycount = 1;
					do {

						er = myapp.Mreader.WriteTagEpcEx(myapp.Rparams.opant,
								data, data.length, rpaswd,
								(short) myapp.Rparams.optime);
						 if(er != READER_ERR.MT_OK_ERR)
	                        	myapp.Mreader.GetLastDetailError(myapp.ei);
						trycount--;
						if (trycount < 1)
							break;
					} while (er != READER_ERR.MT_OK_ERR);

					if (er == READER_ERR.MT_OK_ERR) {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_ok + er.toString(),
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_failed + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				finally{
					 ClearFiler();
				}

			}

		});

		button_testwrite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					SetOpant();
					SetPassword();
					SetFiler();
 
					
					Random rd=new Random(System.currentTimeMillis());
					EditText etadr = (EditText) findViewById(R.id.editText_startaddrw);

					byte[] data = new byte[12];
				    rd.nextBytes(data);
				    EditText_sub3wdata.setText(Reader.bytes_Hexstr(data));

					byte[] rpaswd = new byte[4];
					if (!myapp.Rparams.password.equals("")) {
						myapp.Mreader.Str2Hex(myapp.Rparams.password,
								myapp.Rparams.password.length(), rpaswd);
					}

					READER_ERR er = READER_ERR.MT_OK_ERR;
					int trycount = 1;
					do {
						er = myapp.Mreader.WriteTagData(myapp.Rparams.opant,
								(char) spinner_bankw.getSelectedItemPosition(),
								Integer.valueOf(etadr.getText().toString()),
								data, data.length, rpaswd,
								(short) myapp.Rparams.optime);
						 if(er != READER_ERR.MT_OK_ERR)
	                        	myapp.Mreader.GetLastDetailError(myapp.ei);
						 
						trycount--;
						if (trycount < 1)
							break;
					} while (er != READER_ERR.MT_OK_ERR);

					if (er == READER_ERR.MT_OK_ERR) {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_ok + er.toString(),
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_failed + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				finally{
					 ClearFiler();
				}
			}

		});
		
		
		/**
		 * 锁标签，gen2协议标签，锁定保留区，则保留区不能被读写，其他区，一旦锁定不能被写
		 */
		button_lockop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					SetOpant();
					SetPassword();
					SetFiler();

					Lock_Obj lobj = null;
					Lock_Type ltyp = null;
					int lbank = spinner_lockbank.getSelectedItemPosition();
					int ltype = spinner_locktype.getSelectedItemPosition();
					if (lbank == 0) {
						lobj = Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD;
						if (ltype == 0)
							ltyp = Lock_Type.ACCESS_PASSWD_UNLOCK;
						else if (ltype == 1)
							ltyp = Lock_Type.ACCESS_PASSWD_LOCK;
						else if (ltype == 2)
							ltyp = Lock_Type.ACCESS_PASSWD_PERM_LOCK;

					} else if (lbank == 1) {
						lobj = Lock_Obj.LOCK_OBJECT_KILL_PASSWORD;
						if (ltype == 0)
							ltyp = Lock_Type.KILL_PASSWORD_UNLOCK;
						else if (ltype == 1)
							ltyp = Lock_Type.KILL_PASSWORD_LOCK;
						else if (ltype == 2)
							ltyp = Lock_Type.KILL_PASSWORD_PERM_LOCK;
					} else if (lbank == 2) {
						lobj = Lock_Obj.LOCK_OBJECT_BANK1;
						if (ltype == 0)
							ltyp = Lock_Type.BANK1_UNLOCK;
						else if (ltype == 1)
							ltyp = Lock_Type.BANK1_LOCK;
						else if (ltype == 2)
							ltyp = Lock_Type.BANK1_PERM_LOCK;
					} else if (lbank == 3) {
						lobj = Lock_Obj.LOCK_OBJECT_BANK2;
						if (ltype == 0)
							ltyp = Lock_Type.BANK2_UNLOCK;
						else if (ltype == 1)
							ltyp = Lock_Type.BANK2_LOCK;
						else if (ltype == 2)
							ltyp = Lock_Type.BANK2_PERM_LOCK;
					} else if (lbank == 4) {
						lobj = Lock_Obj.LOCK_OBJECT_BANK3;
						if (ltype == 0)
							ltyp = Lock_Type.BANK3_UNLOCK;
						else if (ltype == 1)
							ltyp = Lock_Type.BANK3_LOCK;
						else if (ltype == 2)
							ltyp = Lock_Type.BANK3_PERM_LOCK;
					}

					byte[] rpaswd = new byte[4];
					;
					if (!myapp.Rparams.password.equals("")) {
						myapp.Mreader.Str2Hex(myapp.Rparams.password,
								myapp.Rparams.password.length(), rpaswd);
					}

					READER_ERR er = myapp.Mreader.LockTag(myapp.Rparams.opant,
							(byte) lobj.value(), (short) ltyp.value(), rpaswd,
							(short) myapp.Rparams.optime);
					if (er == READER_ERR.MT_OK_ERR) {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_sub3lockok,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_sub3lockfail,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				finally{
					 ClearFiler();
				}

			}

		});

		/**
		 * 销毁标签，使标签不能再使用
		 */
		button_kill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				try {
					SetOpant();
					SetFiler();
					EditText et = (EditText) findViewById(R.id.editText_password);
					byte[] rpaswd = new byte[4];
					if (!et.getText().toString().equals("")) {
						myapp.Mreader.Str2Hex(et.getText().toString(), et
								.getText().toString().length(), rpaswd);
					}

					READER_ERR er = myapp.Mreader.KillTag(myapp.Rparams.opant,
							rpaswd, (short) myapp.Rparams.optime);

					if (er == READER_ERR.MT_OK_ERR) {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_killok, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(Sub3TabActivity.this,
								MyApplication.Constr_killfailed,
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				finally{
					 ClearFiler();
				}

			}

		});
		
		button_optimeset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				EditText et_opstr=(EditText) findViewById(R.id.editText_optime);
				try{
				myapp.Rparams.optime=Integer.valueOf(et_opstr.getText().toString());
				}catch(Exception ex)
				{
					Toast.makeText(Sub3TabActivity.this,
							MyApplication.Constr_excep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});	

		tabHost_op.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				int j = tabHost_op.getCurrentTab();
				TabWidget tabIndicator = tabHost_op.getTabWidget();
				View vw = tabIndicator.getChildAt(j);
				vw.setBackgroundColor(Color.BLUE);
				int tc = tabHost_op.getTabContentView().getChildCount();
				for (int i = 0; i < tc; i++) {
					if (i != j) {
						View vw2 = tabIndicator.getChildAt(i);
						vw2.setBackgroundColor(Color.TRANSPARENT);
					}
				}

			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - myapp.exittime) > 2000) {
				Toast.makeText(getApplicationContext(),
						MyApplication.Constr_Putandexit, Toast.LENGTH_SHORT)
						.show();
				myapp.exittime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
