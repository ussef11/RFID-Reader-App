package com.example.module_android_demo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jxl.write.WriteException;

import com.function.*;
import com.function.ScreenListener.ScreenStateListener;
import com.pow.api.cls.RfidPower;
import com.pow.api.cls.RfidPower.PDATYPE;
import com.tools.DjxlExcel;
import com.tools.dlog;
import com.uhf.api.cls.ErrInfo;
import com.uhf.api.cls.ReadExceptionListener;
import com.uhf.api.cls.ReadListener;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.AntPower;
import com.uhf.api.cls.Reader.AntPowerConf;
import com.uhf.api.cls.Reader.CustomCmdType;
import com.uhf.api.cls.Reader.EmbededData_ST;
import com.uhf.api.cls.Reader.HardwareDetails;
import com.uhf.api.cls.Reader.HoptableData_ST;
import com.uhf.api.cls.Reader.Inv_Potl;
import com.uhf.api.cls.Reader.Inv_Potls_ST;
import com.uhf.api.cls.Reader.Module_Type;
import com.uhf.api.cls.Reader.Mtr_Param;
import com.uhf.api.cls.Reader.NXP_U8_InventoryModePara;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.Region_Conf;
import com.uhf.api.cls.Reader.SL_TagProtocol;
import com.uhf.api.cls.Reader.TAGINFO;
import com.uhf.api.cls.Reader.TagFilter_ST;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.PopupMenu;
import android.app.Application;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 *
 * @author Administrator 主要的活动页面，完成程序初始化以，演示RFID盘点功能。
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	ExpandableListView tab4_left, tab4_right;
	TextView tv_once, tv_state, tv_tags, tv_costt, tv_timcost, tv_statics;
	Button button_read, button_stop, button_clear, button_modeset;
	private ListView listView;

	Map<String, TAGINFO> TagsMap = new LinkedHashMap<String, TAGINFO>();// 有序(sorted)
	private Handler handler = new Handler();
	private MyApplication myapp;
	private SoundPool soundPool;
	TabHost tabHost;
	ScreenListener l;
	AndroidWakeLock Awl;
	int Test_count = 0;
    boolean isdefaultmaxpow_V=false;

	// RULE 为true时候 可以设置默认平台(RULE when is true,that default one PDA platform)
	boolean RULE_NOSELPT = false;
	PDATYPE PT = PDATYPE.NONE;

	List<Map<String, ?>> ListMs = new ArrayList<Map<String, ?>>();
	MyAdapter Adapter;
	Map<String, String> h = new HashMap<String, String>();
	long statenvtick;

	// 权限
	boolean right_reg = true;

	// 是否统计

	List<String> VstaticTags = new ArrayList<String>();
	public int Scount = 0;
	Lock lockobj = new ReentrantLock(), locktagsm = new ReentrantLock(); // 注意这个地方

	long vstaticstarttick;
	int vmaxstaticcount;
	int Testcount;
	float Vallcount;
	long Valltime;
	int allrdcnt, avgcnt;
	String strlog;
	int totalcountlast = 0;
	Thread staticthread;
	boolean isrun;
	DjxlExcel dexel;
	int batt_level;
	int batt_scale;
	long runtime;
	int totalcount;
	int staticeveryt=1000;

	int Conidx_sort = 0;
	int Conidx_epcid = 1;
	int Conidx_count = 2;
	int Conidx_ant = 3;
	int Conidx_pro = 4;
	int Conidx_rssi = 5;
	int Conidx_fre = 6;
	int Conidx_rfu = 7;
	int Conidx_tis = 8;
	int Conidx_data = 9;
	int Conidx_u8tid = 10;
	int Conidx_u8bid = 11;

	private ArrayAdapter<String> arrdp_qmode;
	public static Spinner spinner_qmode;

	List<String> Gpodemoauthortags = new ArrayList<String>();
	boolean Tagdis_isfound=false;
	int Tagdis_soundcycle=100;
	int Tagdis_all_rssi=0;
	int Tagdis_all_count=0;

	/**
	 * 
	 * @author Administrator 监听标签广播事件
	 */
	public class MyBroadcastReceiver extends BroadcastReceiver {
		public static final String TAG = "MyBroadcastReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(BROADCAST_ACTION1)) {
				TAGINFO tfs = myapp.Mreader.new TAGINFO();
				tfs.AntennaID = intent.getByteExtra("ANT", (byte) 1);
				tfs.CRC = intent.getByteArrayExtra("CRC");
				tfs.EmbededData = intent.getByteArrayExtra("EMD");
				tfs.EmbededDatalen = intent.getShortExtra("EML", (short) 0);

				tfs.EpcId = intent.getByteArrayExtra("EPC");
				tfs.Epclen = intent.getShortExtra("EPL", (short) 0);
				tfs.Frequency = intent.getIntExtra("FRQ", 0);
				tfs.PC = intent.getByteArrayExtra("PC");
				tfs.Phase = intent.getIntExtra("PHA", 0);
				tfs.ReadCnt = intent.getIntExtra("RDC", 0);
				tfs.Res = intent.getByteArrayExtra("RES");
				tfs.RSSI = intent.getIntExtra("RSI", 0);
				tfs.TimeStamp = intent.getIntExtra("TSP", 0);

				TagsBufferResh(Reader.bytes_Hexstr(tfs.EpcId), tfs);

				tv_tags.setText(String.valueOf(totalcount));
				long readtime = System.currentTimeMillis() - statenvtick;
				tv_timcost.setText("   rt:" + String.valueOf(readtime));

				Adapter.notifyDataSetChanged();
			} else if (intent.getAction().equals(BROADCAST_ACTION2)) {
				if (button_read.isEnabled())
					button_read.performClick();
				dlog.toDlog("power up");
			} else if (intent.getAction().equals(BROADCAST_ACTION3)) {
				if (button_stop.isEnabled())
					button_stop.performClick();
				dlog.toDlog("power down");
			} else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				batt_level = intent.getIntExtra("level", 0);
				batt_scale = intent.getIntExtra("scale", 0);
			}
		}
	}

	/**
	 * 处理线程中的事物
	 */
	public Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bd = msg.getData();
			switch (msg.what) {
			case 0: {
				tv_tags.setText(bd.get("Msg_all").toString());
				tv_timcost.setText("   rt:" + bd.get("Msg_cot").toString());

				handler.removeCallbacks(runnable_MainActivity);

				break;

			}
			case 1: {
				StopFunc(false);

				break;
			}
			case 2: {
				ErrInfo ei = new ErrInfo();
				myapp.Mreader.GetLastDetailError(ei);
				Test_count++;
				tv_state.setText(bd.get("Msg_error_2").toString() + " "
						+ String.valueOf(Test_count) + ei.errstr);

				break;
			}
			case 3: {

				StopHandleUI();
				break;
			}
			case 4: {
				tv_statics.setText(bd.get("Msg_sec").toString());
				break;
			}
			case 5: {
				ListMs.clear();
				ListMs.add(h);
				Adapter.notifyDataSetChanged();
				ReadHandleUI();
				handler.postDelayed(runnable_MainActivity, 0);
				break;
			}
			case 6: {
				ListMs.clear();
				// showlist();

				ListMs.add(h);
				Adapter.notifyDataSetChanged();
			}
			}
		}
	};

	public static boolean runDevchmod(String binpath, String devpath) {
		try {
			/* Missing read/write permission, trying to chmod the file */
			Process su;
			su = Runtime.getRuntime().exec(binpath);
			String cmd = "chmod 666 " + devpath + "\n" + "exit\n";
			su.getOutputStream().write(cmd.getBytes());
			if (su.waitFor() != 0) {
				throw new SecurityException("securityexception");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("MYINFO", "runDevchmod Exception e = " + e);

		}
		return true;
	}

	public static boolean runRootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;

		try {
			process = Runtime.getRuntime().exec("sh");// Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("Phone Link",
					"su root - the device is not rooted,  error message： "
							+ e.getMessage());
			return false;
		} finally {
			try {
				if (null != os) {
					os.close();
				}
				if (null != process) {
					process.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/***
	 * 重连接读写器
	 * 
	 * @return
	 */
	private boolean reconnect() {
		Log.d("MYINFO", "reconenct");
		boolean blen = myapp.Rpower.PowerUp();
		if (!blen)
			return false;
		Toast.makeText(MainActivity.this,
				MyApplication.Constr_mainpu + String.valueOf(blen),
				Toast.LENGTH_SHORT).show();

		READER_ERR er = myapp.Mreader.InitReader_Notype(myapp.Address,
				myapp.antportc);
		if (er == READER_ERR.MT_OK_ERR) {
			tv_state.setText("");
			myapp.needreconnect = false;
			try {

				// myapp.Rparams = myapp.spf.ReadReaderParams();

				if (myapp.Rparams.invpro.size() < 1)
					myapp.Rparams.invpro.add("GEN2");

				List<SL_TagProtocol> ltp = new ArrayList<SL_TagProtocol>();
				for (int i = 0; i < myapp.Rparams.invpro.size(); i++) {
					if (myapp.Rparams.invpro.get(i).equals("GEN2")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);

					} else if (myapp.Rparams.invpro.get(i).equals("6B")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);

					} else if (myapp.Rparams.invpro.get(i).equals("IPX64")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);

					} else if (myapp.Rparams.invpro.get(i).equals("IPX256")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);

					}
				}

				Inv_Potls_ST ipst = myapp.Mreader.new Inv_Potls_ST();
				ipst.potlcnt = ltp.size();
				ipst.potls = new Inv_Potl[ipst.potlcnt];
				SL_TagProtocol[] stp = ltp
						.toArray(new SL_TagProtocol[ipst.potlcnt]);
				for (int i = 0; i < ipst.potlcnt; i++) {
					Inv_Potl ipl = myapp.Mreader.new Inv_Potl();
					ipl.weight = 30;
					ipl.potl = stp[i];
					ipst.potls[0] = ipl;
				}

				er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL,
						ipst);
				Log.d("MYINFO", "Connected set pro:" + er.toString());

				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
						new int[] { myapp.Rparams.checkant });
				Log.d("MYINFO", "Connected set checkant:" + er.toString());

				AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
				apcf.antcnt = myapp.antportc;
				for (int i = 0; i < apcf.antcnt; i++) {
					AntPower jaap = myapp.Mreader.new AntPower();
					jaap.antid = i + 1;
					jaap.readPower = (short) myapp.Rparams.rpow[i];
					jaap.writePower = (short) myapp.Rparams.wpow[i];
					apcf.Powers[i] = jaap;
				}

				myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);

				Region_Conf rre;
				switch (myapp.Rparams.region) {
				case 0:
					rre = Region_Conf.RG_PRC;
					break;
				case 1:
					rre = Region_Conf.RG_NA;
					break;
				case 2:
					rre = Region_Conf.RG_NONE;
					break;
				case 3:
					rre = Region_Conf.RG_KR;
					break;
				case 4:
					rre = Region_Conf.RG_EU;
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				default:
					rre = Region_Conf.RG_NONE;
					break;
				}
				if (rre != Region_Conf.RG_NONE) {
					er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rre);
				}

				if (myapp.Rparams.frelen > 0) {

					HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
					hdst.lenhtb = myapp.Rparams.frelen;
					hdst.htb = myapp.Rparams.frecys;
					er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
				}

				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
						new int[] { myapp.Rparams.session });
				er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q,
						new int[] { myapp.Rparams.qv });
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE,
						new int[] { myapp.Rparams.wmode });
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN,
						new int[] { myapp.Rparams.maxlen });
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET,
						new int[] { myapp.Rparams.target });

				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA,
						new int[] { myapp.Rparams.adataq });
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI,
						new int[] { myapp.Rparams.rhssi });
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE,
						new int[] { myapp.Rparams.invw });

				NXP_U8_InventoryModePara u8para = myapp.Mreader.new NXP_U8_InventoryModePara();

				u8para.Mode[0] = 0;

				if (myapp.nxpu8 == 0) {
					if (myapp.Rparams.filenable == 1) {
						TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();
						tfst.bank = myapp.Rparams.filbank;
						int len = myapp.Rparams.fildata.length();
						len = len % 2 == 0 ? len : len + 1;
						tfst.fdata = new byte[len / 2];
						myapp.Mreader.Str2Hex(myapp.Rparams.fildata,
								myapp.Rparams.fildata.length(), tfst.fdata);
						tfst.flen = myapp.Rparams.fildata.length() * 4;
						tfst.startaddr = myapp.Rparams.filadr;
						tfst.isInvert = myapp.Rparams.filisinver;

						myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER,
								tfst);
					}

					if (myapp.Rparams.emdenable == 1) {
						EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();

						edst.accesspwd = null;
						edst.bank = myapp.Rparams.emdbank;
						edst.startaddr = myapp.Rparams.emdadr;
						edst.bytecnt = myapp.Rparams.emdbytec;

						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
					}
				} else if (myapp.nxpu8 == 1) {
					TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();

					tfst.fdata = new byte[1];
					tfst.fdata[0] = (byte) 0x80;
					tfst.bank = 1;
					tfst.flen = 1;
					tfst.startaddr = 0x204;
					tfst.isInvert = 0;
					er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER,
							tfst);

					EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();
					edst.accesspwd = null;

					edst.bank = 2;
					edst.startaddr = 0;
					edst.bytecnt = 12;

					er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
					if (er == READER_ERR.MT_OK_ERR) {
					}

					u8para.Mode[0] = 1;
					myapp.nxpu8 = 1;
				} else if (myapp.nxpu8 == 2) {
					TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();

					tfst.fdata = new byte[1];
					tfst.fdata[0] = (byte) 0x80;
					tfst.bank = 1;
					tfst.flen = 1;
					tfst.startaddr = 0x203;
					tfst.isInvert = 0;
					er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER,
							tfst);

					u8para.Mode[0] = 1;
					myapp.nxpu8 = 2;
				} else if (myapp.nxpu8 == 3) {
					TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();

					tfst.fdata = new byte[1];
					tfst.fdata[0] = (byte) 0x80;
					tfst.bank = 1;
					tfst.flen = 1;
					tfst.startaddr = 0x204;
					tfst.isInvert = 0;
					er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER,
							tfst);
					u8para.Mode[0] = 1;
					myapp.nxpu8 = 3;
				}
				myapp.Mreader.CustomCmd(0, CustomCmdType.NXP_U8_InventoryMode,
						u8para, null);

			} catch (Exception ex) {
				Log.d("MYINFO",
						ex.getMessage() + ex.toString() + ex.getStackTrace());
			}
		} else
			return false;

		return true;
	}

	// ****************************************事件方式读取标签(event way for get tags)
	MyBroadcastReceiver mBroadcastReceiver = new MyBroadcastReceiver();
	public static final String BROADCAST_ACTION1 = "com.example.module_android_demo";
	public static final String BROADCAST_ACTION2 = "com.android.action.keyevent.KEYCODE_KEYCODE_UHF_UP";
	public static final String BROADCAST_ACTION3 = "com.android.action.keyevent.KEYCODE_KEYCODE_UHF_DOWN";

	READER_ERR StartReadTags() {
		// 初始化结BackReadOption(init BackReadOption)

		// 本例只使用天线1进行盘存，如果要使用多个天线则只需要将使用的天线编
		// 号赋值到数组ants中，例如同时使用天线1和2，则代码应该改为ants[0] = 1;
		// ants[1] = 2;antcnt = 2;
		// (the sample only use ant 1 to inventory,if use multiple
		// than use ants arrays with antid, as use ant1 and ant2 at the
		// same,ants[0] = 1;
		// ants[1] = 2;antcnt = 2;)

		/*
		 * 是否采用高速模式（目前只有slr11xx和slr12xx系列读写器才支持）,对于
		 * 一般标签数量不大，速度不快的应用没有必要使用高速模式,本例没有设置使用高速模式 (whether use high speed
		 * mode(only slr11xx and slr12xx series readers supported)，for not many
		 * of tags normal，is needn't to use)
		 */
		if (myapp.isquicklymode)
			myapp.m_BROption.IsFastRead = true;
		else
			myapp.m_BROption.IsFastRead = false;// 采用非高速模式盘存标签（not use high
												// speed）

		// /非高速盘存模式下必须要设置的参数*******************************************
		// 盘存周期,单位为ms，可根据实际使用的天线个数按照每个天线需要200ms
		// 的方式计算得出,如果启用高速模式则此选项没有任何意义，可以设置为
		// 任意值，或者干脆不设置
		// (in not high speed mode,that must set some params
		// inventory cycle,as ms unit.according to antenna count and each
		// antenna read time as 200ms to
		// get the inventory cycle.is invalid in high speed mode.)
		myapp.m_BROption.ReadDuration = (short) (myapp.Rparams.readtime * myapp.Rparams.uants.length);
		// 盘存周期间的设备不工作时间,单位为ms,一般可设置为0，增加设备不工作
		// 时间有利于节电和减少设备发热（针对某些使用电池供电或空间结构不利
		// 于散热的情况会有帮助）
		// (it is the time that the reader is not working during inventory
		// cycle,as ms unit,generally is set 0
		// increase the sleep time is in favour of save electricity and reduce
		// heating (specially using battery or the space
		// is not good for heat dissipation))
		myapp.m_BROption.ReadInterval = myapp.Rparams.sleep;
		// ****************************************************************************

		// 高速盘存模式参数设置********************************************************
		// (params with high speed mode)
		// 以下为选择使用高速模式才起作用的选项参,照如下设置即可,如果使能高速
		// 模式，即把IsFastRead设置为true则,只需取消以下被注释的代码即可
		/*
		 * (the parameters as fallow is avaible in high speed mode,set as
		 * fallow. set IsFastRead as true, if enable the high speed mode,and
		 * cancel the Following code) // 高速模式下为取得最佳效果设置为0即可(set to 0 to get the
		 * best effect in high speed mode)
		 */
		if (myapp.m_BROption.IsFastRead) {
			myapp.m_BROption.FastReadDutyRation = 0;

			// 标签信息是否携带识别天线的编号(if with antenna id of tag info)
			myapp.m_BROption.TMFlags.IsAntennaID = true; //

			// 标签信息是否携带标签识别次数(if with read count of tag info)
			myapp.m_BROption.TMFlags.IsReadCnt = false;

			// 标签信息是否携带识别标签时的信号强度 (if with rssi of tag info)
			myapp.m_BROption.TMFlags.IsRSSI = false;

			// 标签信息是否携带时间戳(if with time stamp of tag info)
			myapp.m_BROption.TMFlags.IsTimestamp = false;

			// 标签信息是否携带识别标签时的工作频点(if withfrequency of tag info)
			myapp.m_BROption.TMFlags.IsFrequency = false;

			// 标签信息是否携带识别标签时同时读取的其它bank数据信息,如果要获取在
			// 盘存时同时读取其它bank的信息还必须设置MTR_PARAM_TAG_EMBEDEDDATA参数,
			// （目前只有slr11xx和slr12xx系列读写器才支持）
			// (if with addition data of tag info
			// read the bank data when inventorying,you must set param
			// ofMTR_PARAM_TAG_EMBEDEDDATA
			// (only slr11xx and slr12xx series reader suppoted)
			myapp.m_BROption.TMFlags.IsEmdData = false;

			// 保留字段，可始终设置为0(reserver field always as 0)
			myapp.m_BROption.TMFlags.IsRFU = false;

		}

		return myapp.Mreader.StartReading(myapp.Rparams.uants,
				myapp.Rparams.uants.length, myapp.m_BROption);
	}

	/**
	 * 异步盘点方式监听标签事件
	 */

	// 标签事件(tag event)
	ReadListener RL = new ReadListener() {
		@Override
		public void tagRead(Reader r, final TAGINFO[] tag) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setAction(BROADCAST_ACTION1);
			if (myapp.issound)
				soundPool.play(1, 1, 1, 0, 0, 1);
			for (int i = 0; i < tag.length; i++) {
				intent.putExtra("ANT", tag[i].AntennaID);
				intent.putExtra("CRC", tag[i].CRC);
				intent.putExtra("EMD", tag[i].EmbededData);
				intent.putExtra("EML", tag[i].EmbededDatalen);

				intent.putExtra("EPC", tag[i].EpcId);
				intent.putExtra("EPL", tag[i].Epclen);
				intent.putExtra("FRQ", tag[i].Frequency);
				intent.putExtra("PC", tag[i].PC);
				intent.putExtra("PHA", tag[i].Phase);
				intent.putExtra("RDC", tag[i].ReadCnt);
				intent.putExtra("RES", tag[i].Res);
				intent.putExtra("RSI", tag[i].RSSI);
				intent.putExtra("TSP", tag[i].TimeStamp);

				sendBroadcast(intent);
			}

		}

	};

	/***
	 * 异步盘点方式监听异常事件
	 * */
	ReadExceptionListener REL = new ReadExceptionListener() {

		@Override
		public void tagReadException(Reader r, final READER_ERR re) {
			// TODO Auto-generated method stub
			Message mes = new Message();
			mes.what = 2;
			Bundle bd = new Bundle();
			bd.putString("Msg_error_2", re.toString());
			mes.setData(bd);
			handler2.sendMessage(mes);
		}

	};

	// ************************************************//

	// select inventory mode
	private boolean selectinventorymode() {
		myapp.issmartmode = false;
		if (spinner_qmode.getSelectedItemPosition() == -1
				|| spinner_qmode.getSelectedItemPosition() == 0)
			myapp.isquicklymode = false;

		else {
			myapp.isquicklymode = true;

			int[] mp = new int[1];
			String msg = ":间隔0,";
			myapp.Rparams.sleep = 0;
			READER_ERR er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_RF_MAXPOWER, mp);
			if (er == READER_ERR.MT_OK_ERR) {

				msg += "PowMax " + String.valueOf((short) mp[0]) + ",";
				AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
				apcf.antcnt = myapp.antportc;
				int[] rpow = new int[apcf.antcnt];
				int[] wpow = new int[apcf.antcnt];
				for (int i = 0; i < apcf.antcnt; i++) {
					AntPower jaap = myapp.Mreader.new AntPower();
					jaap.antid = i + 1;
					jaap.readPower = (short) (mp[0]);
					rpow[i] = jaap.readPower;

					jaap.writePower = (short) (mp[0]);
					wpow[i] = jaap.writePower;
					apcf.Powers[i] = jaap;
				}
				er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER,
						apcf);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.rpow = rpow;
					myapp.Rparams.wpow = wpow;

				} else
					return false;
			} else
				return false;

			if (spinner_qmode.getSelectedItemPosition() == 1) {
				msg += "Session 0,";
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 0 });
				if (er == READER_ERR.MT_OK_ERR)
					myapp.Rparams.session = 0;
				else
					return false;

				if (myapp.myhd.module == Module_Type.MODOULE_SLR1200) {
					myapp.Rparams.option |= 0x10;
					msg += "HM";
				}
			} else if (spinner_qmode.getSelectedItemPosition() == 2) {
				msg += "Session 1";
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 1 });
				if (er == READER_ERR.MT_OK_ERR)
					myapp.Rparams.session = 1;
				else
					return false;
			} else if (spinner_qmode.getSelectedItemPosition() == 3) {
				msg += "Session 1,";
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 1 });
				if (er == READER_ERR.MT_OK_ERR)
					myapp.Rparams.session = 1;
				else
					return false;

				if (myapp.myhd.module == Module_Type.MODOULE_SLR1200) {
					myapp.Rparams.option |= 0x10;
					msg += "HM";
				}

			} else if (spinner_qmode.getSelectedItemPosition() == 4) {
				msg += "Session 1,";
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 1 });
				if (er == READER_ERR.MT_OK_ERR)
					myapp.Rparams.session = 1;
				else
					return false;

				if (myapp.myhd.module == Module_Type.MODOULE_SLR1200) {
					myapp.Rparams.option |= 0x10;
					msg += "HM,";
				}
				msg += "Smart,盘点控制 循环将不可用";
				myapp.smartmode=Reader.IT_MODE.IT_MODE_CT;
				myapp.issmartmode = true;
			}
			else if (spinner_qmode.getSelectedItemPosition() == 5) {
				myapp.smartmode=Reader.IT_MODE.IT_MODE_S2;
				myapp.issmartmode = true;
			}
			else if (spinner_qmode.getSelectedItemPosition() == 6) {
				myapp.smartmode=Reader.IT_MODE.IT_MODE_E7;
				myapp.issmartmode = true;
			}
			else if (spinner_qmode.getSelectedItemPosition() == 7) {
				myapp.smartmode=Reader.IT_MODE.IT_MODE_E7v2;
				myapp.issmartmode = true;
			}
			else if (spinner_qmode.getSelectedItemPosition() == 8) {
				 /*no test the way
                Reader.CustomParam_ST cpst=myapp.Mreader.new CustomParam_ST();
                cpst.ParamName="Reader/Ex10fastmode";
                byte[] vals=new byte[22];
                vals[0]=1;
                vals[1]=20;
                for(int i=0;i<20;i++)
                    vals[2+i]=0;
                cpst.ParamVal=vals;
                myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_CUSTOM, cpst);

				  */
			}
/*
			if(spinner_qmode.getSelectedItemPosition() != 8)
            {
                Reader.CustomParam_ST cpst=myapp.Mreader.new CustomParam_ST();
                cpst.ParamName="Reader/Ex10fastmode";
                byte[] vals=new byte[22];
                vals[0]=0;
                vals[1]=20;
                for(int i=0;i<20;i++)
                    vals[2+i]=0;
                cpst.ParamVal=vals;
            }*/
			Toast.makeText(MainActivity.this, MyApplication.Constr_set + msg,
					Toast.LENGTH_SHORT).show();
		}
		myapp.qmode = MainActivity.spinner_qmode.getSelectedItemPosition();
		if (Sub4TabActivity.spinner_qmode != null)
			Sub4TabActivity.spinner_qmode
					.setSelection(MainActivity.spinner_qmode
							.getSelectedItemPosition());
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.spf = new SPconfig(this);
		// 设置语言
		setLange();
		String apkRoot = "chmod 777 " + getPackageCodePath();
		// 设置权限
		runRootCommand(apkRoot);
		myapp.Mreader = new Reader();
		// for custom test
		// runDevchmod("/system/xbin/su", "/dev/ttyMT1");

		// 初始化声音对象
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(this, R.raw.beep333, 1);
		// 唤醒锁
		Awl = new AndroidWakeLock(
				(PowerManager) getSystemService(Context.POWER_SERVICE));


		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		inputMethodManager.hideSoftInputFromWindow(MainActivity.this
						.getCurrentFocus().getWindowToken(),

				InputMethodManager.HIDE_NOT_ALWAYS);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		if (RULE_NOSELPT)
			;
		else
			tabHost.addTab(tabHost
					.newTabSpec("tab1")
					.setIndicator(MyApplication.Constr_CONNECT,
							getResources().getDrawable(R.drawable.ic_launcher))
					.setContent(new Intent(this, Sub1TabActivity.class)));

		TabSpec tp = tabHost.newTabSpec("tab2")
				.setIndicator(MyApplication.Constr_INVENTORY)
				.setContent(R.id.tab2);

		tabHost.addTab(tp);
		tabHost.addTab(tabHost
				.newTabSpec("tab3")
				.setIndicator(
						MyApplication.Constr_RWLOP,
						getResources().getDrawable(
								android.R.drawable.arrow_down_float))
				.setContent(new Intent(this, Sub3TabActivity.class)));

		tabHost.addTab(tabHost
				.newTabSpec("tab4")
				.setIndicator(
						MyApplication.Constr_set,
						getResources().getDrawable(
								android.R.drawable.arrow_down_float))
				.setContent(new Intent(this, Sub4TabActivity.class)));

		TabWidget tw = tabHost.getTabWidget();
		if (RULE_NOSELPT)
			;
		else {
			tw.getChildAt(1).setVisibility(View.INVISIBLE);
			tw.getChildAt(2).setVisibility(View.INVISIBLE);
			tw.getChildAt(3).setVisibility(View.INVISIBLE);
		}
		myapp.Rparams = myapp.new ReaderParams();
		Coname = MyApplication.Coname;

		mBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BROADCAST_ACTION1);
		intentFilter.addAction(BROADCAST_ACTION2);
		intentFilter.addAction(BROADCAST_ACTION3);
		// intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBroadcastReceiver, intentFilter);

		myapp.tabHost = tabHost;
		button_read = (Button) findViewById(R.id.button_start);
		button_stop = (Button) findViewById(R.id.button_stop);
		button_stop.setEnabled(false);
		button_clear = (Button) findViewById(R.id.button_readclear);
		button_modeset = (Button) findViewById(R.id.button_invenmodeset);
		listView = (ListView) findViewById(R.id.listView_epclist);

		tv_once = (TextView) findViewById(R.id.textView_readoncecnt);
		tv_state = (TextView) findViewById(R.id.textView_invstate);
		tv_tags = (TextView) findViewById(R.id.textView_readallcnt);
		tv_costt = (TextView) findViewById(R.id.textView_costtime);
		tv_timcost = (TextView) findViewById(R.id.textView_costaltime);
		tv_statics = (TextView) findViewById(R.id.textView_cavs);

		spinner_qmode = (Spinner) findViewById(R.id.spinner_invmode);
		arrdp_qmode = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, myapp.spiqmode);
		arrdp_qmode
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_qmode.setAdapter(arrdp_qmode);

		for (int i = 0; i < Coname.length; i++)
			h.put(Coname[i], Coname[i]);
		myapp.needreconnect = false;
		l = new ScreenListener(this);

		if (RULE_NOSELPT) {
			myapp.spf = new SPconfig(this);
			myapp.Rpower = new RfidPower(PT, getApplicationContext());
			String path = myapp.Rpower.GetDevPath();
			if (myapp.Rpower.PowerUp()) {
				READER_ERR er = myapp.Mreader.InitReader_Notype(path, 1);
				myapp.antportc = 1;
				myapp.Address = path;
				HardwareDetails val = myapp.Mreader.new HardwareDetails();
				er = myapp.Mreader.GetHardwareDetails(val);
				myapp.myhd = val;
				if (er == READER_ERR.MT_OK_ERR) {
					ConnectHandleUI();
				}
			}
		}



		//开启软件自动上电


		Intent pintent = new Intent("android.intent.action.SETTINGS_BJ");
		pintent.putExtra( "enable" , true);
		getApplicationContext().sendBroadcast(pintent);

		int portc = 1;
        String ip = "/dev/ttyS1";
		myapp.dv=new Reader.deviceVersion();
		Reader.GetDeviceVersion(ip, myapp.dv);


		long const_v = System.currentTimeMillis();
		READER_ERR er = myapp.Mreader.InitReader_Notype(ip, portc);
		Log.d("MYINFO",
				"connect cost time:"
						+ String.valueOf(System.currentTimeMillis()
						- const_v));

		if (er == READER_ERR.MT_OK_ERR) {
			myapp.needlisen = true;
			myapp.needreconnect = false;
			myapp.spf.SaveString("PDATYPE", "1");
			myapp.spf.SaveString("ADDRESS",ip);
			myapp.spf.SaveString("ANTPORT", "1");

			// Toast.makeText(Sub1TabActivity.this,
			// MyApplication.Constr_connectok, Toast.LENGTH_SHORT)
			// .show();

			Inv_Potls_ST ipst = myapp.Mreader.new Inv_Potls_ST();
			ipst.potlcnt = 1;
			ipst.potls = new Inv_Potl[ipst.potlcnt];
			for (int i = 0; i < ipst.potlcnt; i++) {
				Inv_Potl ipl = myapp.Mreader.new Inv_Potl();
				ipl.weight = 30;
				ipl.potl = SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
				ipst.potls[i] = ipl;
			}
			er = myapp.Mreader.ParamSet(
					Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
			int[] av = new int[1];
			myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_READER_AVAILABLE_ANTPORTS, av);
			myapp.antportc = portc;


			ConnectHandleUI1();
			myapp.Address = ip;
			myapp.haveinit=true;

		} else {

			Toast.makeText(
					MainActivity.this,
					MyApplication.Constr_connectfialed + ":"
							+ er.toString(), Toast.LENGTH_SHORT).show();
		}




		// 屏幕监听
		l.begin(new ScreenStateListener() {

			@Override
			public void onScreenOn() {

			}

			@Override
			public void onScreenOff() {

				Log.d("MYINFO", "onScreenoff");
				if (button_stop.isEnabled()) {
					button_stop.performClick();
				}

				if (myapp.Mreader != null) {
					myapp.Mreader.CloseReader();
					myapp.needlisen = true;
				}

				if (myapp.Rpower != null) {
					myapp.Rpower.PowerDown();
					myapp.needreconnect = true;
				}
			}
		});

		/**
		 * 开始盘点
		 */
		button_read.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				try {
					if (myapp.islatetime) {
						Thread.sleep(myapp.latetimems);
					}

					if (Adapter == null) {
						ListMs.add(h);
						Adapter = new MyAdapter(getApplicationContext(),
								ListMs, R.layout.listitemview_inv, Coname,
								new int[] { R.id.textView_readsort,
										R.id.textView_readepc,
										R.id.textView_readcnt,
										R.id.textView_readant,
										R.id.textView_readpro,
										R.id.textView_readrssi,
										R.id.textView_readfre,
										R.id.textView_readrfu,
										R.id.textView_readtis,
										R.id.textView_reademd,
										R.id.textView_readnxpu8_tid,
										R.id.textView_readnxpu8_bid });

						listView.setAdapter(Adapter);
						if (ListMs.size() > 0) {
							String data = ListMs.get(0).toString();
							String message = "Data to display: " + data;
							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
						}
					}

					// 判断是否要重连接
					boolean bl = true;
					if (myapp.needreconnect) {
						int c = 0;
						do {
							bl = reconnect();
							if (!bl)
								Toast.makeText(MainActivity.this,
										MyApplication.Constr_sub1recfailed,
										Toast.LENGTH_SHORT).show();
							c++;
							if (c > 0)
								break;
						} while (true);
					}
					if (!bl)
						return;

					// 产生时间数量报表
					if (myapp.isReport_rec) {
						dexel = new DjxlExcel("时间数量报表");

						String[] s2 = new String[] { "测试次数", "标签次数", "标签个数",
								"新增个数", "总数", "时间(毫秒)" };
						List<String> ls = new ArrayList<String>();
						for (int i = 0; i < s2.length; i++)
							ls.add(s2[i]);
						try {
							dexel.CreatereExcelfile(ls);
						} catch (WriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// 电量模块报表
					if (myapp.isReport_tep) {
						dexel = new DjxlExcel("电量温度报表");

						String[] s2 = new String[] { "时间", "温度", "电量%" };
						List<String> ls = new ArrayList<String>();
						for (int i = 0; i < s2.length; i++)
							ls.add(s2[i]);
						try {
							String linestr = myapp.myhd.module.toString()
									+ "   ";

							int cnp = Sub4TabActivity.nowpower;
							int mu = (int) (myapp.stoptimems / 60000);
							linestr += String.valueOf(cnp) + " "
									+ String.valueOf(mu);

							dexel.CreatereExcelfile(linestr + "分钟记录一次", ls);

							mBroadcastReceiver = new MyBroadcastReceiver();
							IntentFilter intentFilter = new IntentFilter();
							intentFilter.addAction(BROADCAST_ACTION1);
							intentFilter.addAction(BROADCAST_ACTION2);
							intentFilter.addAction(BROADCAST_ACTION3);
							intentFilter
									.addAction(Intent.ACTION_BATTERY_CHANGED);
							registerReceiver(mBroadcastReceiver, intentFilter);

						} catch (WriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// for quickly mode test
					/**
					 * isquicklymode 为true 即快速模式,为false 即普通模式 threadmode 0即线程盘点
					 * threadmode 1 为标签事件监听模式
					 */
					if (myapp.ThreadMODE == 0) {

						if (myapp.issmartmode) {
							if (myapp.needlisen == true) {
								myapp.Mreader.addReadListener(RL);

								myapp.Mreader.addReadExceptionListener(REL);
								myapp.needlisen = false;
							}

							READER_ERR er = myapp.Mreader
									.AsyncStartReading_IT(myapp.smartmode,
											myapp.Rparams.uants,
											myapp.Rparams.uants.length,
											myapp.Rparams.option);
							if (er != READER_ERR.MT_OK_ERR) {
								myapp.Mreader.GetLastDetailError(myapp.ei);
								Toast.makeText(MainActivity.this,
										MyApplication.Constr_nostopreadfailed,
										Toast.LENGTH_SHORT).show();
								return;
							}

						} else {
							if (myapp.isquicklymode) {
								READER_ERR er;
								/*
								if(myapp.qmode==8)
								{
									byte[] temp=new byte[20];

									er = myapp.Mreader
											.AsyncStartReadingEx(myapp.Rparams.uants,
													myapp.Rparams.uants.length,
													myapp.Rparams.option,temp);
								}
								else

								 */
								    {
									 er = myapp.Mreader
											.AsyncStartReading(myapp.Rparams.uants,
													myapp.Rparams.uants.length,
													myapp.Rparams.option);
								}
								if (er != READER_ERR.MT_OK_ERR) {
									myapp.Mreader.GetLastDetailError(myapp.ei);
									Toast.makeText(
											MainActivity.this,
											MyApplication.Constr_nostopreadfailed,
											Toast.LENGTH_SHORT).show();
									return;
								}
							}

							handler.postDelayed(runnable_MainActivity, 0);
						}

					} else if (myapp.ThreadMODE == 1) {
						if (myapp.needlisen == true)
						// 设置盘存到标签时的回调处理函数(set the callback function for
						// inventory tags)
						{
							myapp.Mreader.addReadListener(RL);
							// *
							// 设置读写器发生错误时的回调处理函数(set the callback function for
							// handling error)
							myapp.Mreader.addReadExceptionListener(REL);
							myapp.needlisen = false;
						}

						// 广播形式(forms of broadcasting)
						if (StartReadTags() != READER_ERR.MT_OK_ERR) {
							Toast.makeText(MainActivity.this,
									MyApplication.Constr_nostopreadfailed,
									Toast.LENGTH_SHORT).show();
							return;
						}
					}

					Testcount = 0;
					strlog = "";
					statenvtick = System.currentTimeMillis();
					vstaticstarttick = System.currentTimeMillis() - statenvtick;
					if (myapp.VisStatics) { // vstatichandler.post(runnable_statics);

						totalcount = 0;
						totalcountlast = 0;
						TagsMap.clear();

						ListMs.clear();
						ListMs.add(h);
						Adapter.notifyDataSetChanged();

						isrun = true;
						staticthread = new Thread(runnable_statics_thread);
						staticthread.start();
					}

					if(myapp.isFoundTag){
						isrun = true;
						staticthread = new Thread(runnable_rssicount_thread);
						staticthread.start();
					}
					Awl.WakeLock();
					myapp.TagsMap.clear();
					ReadHandleUI();

				} catch (Exception ex) {
					Toast.makeText(
							MainActivity.this,
							MyApplication.Constr_nostopreadfailed
									+ ex.getMessage(), Toast.LENGTH_SHORT)
							.show();
				}
			}

		});

		/**
		 * 停止盘点
		 */
		button_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				StopFunc(true);
			}

		});

		/**
		 * 清空列表以及缓冲数据
		 */
		button_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (Adapter != null) {
					TagsMap.clear();
					myapp.TagsMap.clear();
					ListMs.clear();
					totalcount = 0;
					totalcountlast = 0;
					// showlist();

					ListMs.add(h);
					Adapter.notifyDataSetChanged();
				}

				tv_once.setText("0");
				tv_tags.setText("0");
				tv_state.setText("...");

				tv_costt.setText("0");
				tv_timcost.setText("0");
				tv_statics.setText(" 0/s,m:0/s");

				if(!myapp.isFoundTag)
				myapp.Curepc = "";

				if (myapp.issmartmode) {
					myapp.Mreader.Reset_IT();
					if (!button_read.isEnabled()) {
						button_stop.performClick();
						button_read.performClick();
					}
				}
			}
		});

		button_modeset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!selectinventorymode()) {
					Toast.makeText(MainActivity.this,
							MyApplication.Constr_failed, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		this.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < listView.getCount(); i++) {
					if (i != arg2) {
						View v = listView.getChildAt(i);
						if (v != null) {
							ColorDrawable cd = (ColorDrawable) v
									.getBackground();
							if (Color.YELLOW == cd.getColor()) {
								int[] colors = { Color.WHITE,
										Color.rgb(219, 238, 244) };// RGB颜色(RGB
																	// color)
								v.setBackgroundColor(colors[i % 2]);// 每隔item之间颜色不同(each
																	// item
																	// color
																	// different)
							}
						}
					}
				}

				arg1.setBackgroundColor(Color.YELLOW);

				@SuppressWarnings("unchecked")
				HashMap<String, String> hm = (HashMap<String, String>) listView
						.getItemAtPosition(arg2);
				String epc = hm.get("EPC ID");
				myapp.Curepc = epc.trim();
				if (myapp.gpodemomode != 0)
					showPopupMenu(arg1);

			}

		});

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {
				int j = tabHost.getCurrentTab();
				if (RULE_NOSELPT) {
					if (j == 1) {
						Sub3TabActivity.EditText_sub3fildata
								.setText(myapp.Curepc);
						Sub3TabActivity.EditText_sub3wdata
								.setText(myapp.Curepc);
					}
				} else {
					if (j == 2) {
						Sub3TabActivity.EditText_sub3fildata
								.setText(myapp.Curepc);
						Sub3TabActivity.EditText_sub3wdata
								.setText(myapp.Curepc);
					}
				}
			}
		});
	}

	String[] Coname;

	private void SortbyEpc() {
		List<Map<String, ?>> ListMsnohead = new ArrayList<Map<String, ?>>();

		for (int i = 1; i < ListMs.size(); i++)
			ListMsnohead.add(ListMs.get(i));

		Collections.sort(ListMsnohead, new Comparator<Map<String, ?>>() {

			@Override
			public int compare(Map<String, ?> arg0, Map<String, ?> arg1) {
				// TODO Auto-generated method stub
				return ((String) arg0.get(Coname[Conidx_epcid]))
						.compareTo((String) arg1.get(Coname[Conidx_epcid]));

			}
		});
		ListMs.clear();
		ListMs.add(h);
		ListMs.addAll(ListMsnohead);
		this.Adapter.notifyDataSetChanged();
	}

	private void StopFunc(boolean isfinal) {

		if (myapp.ThreadMODE == 0) {

			if (myapp.issmartmode) {
				READER_ERR er = myapp.Mreader.AsyncStopReading_IT();
				if (er != READER_ERR.MT_OK_ERR) {
					myapp.Mreader.GetLastDetailError(myapp.ei);
					Toast.makeText(MainActivity.this,
							MyApplication.Constr_nostopspreadfailed,
							Toast.LENGTH_SHORT).show();
					return;
				}
			} else {
				if (myapp.isquicklymode) {
					// dlog.toDlog("stop---");
					READER_ERR er;

					/*if(myapp.qmode==8)
					er= myapp.Mreader.AsyncStopReadingEx();
						else

					 */
					er= myapp.Mreader.AsyncStopReading();
					if (er != READER_ERR.MT_OK_ERR) {
						myapp.Mreader.GetLastDetailError(myapp.ei);
						Toast.makeText(MainActivity.this,
								MyApplication.Constr_nostopspreadfailed,
								Toast.LENGTH_SHORT).show();
						return;
					}

				}
				handler.removeCallbacks(runnable_MainActivity);

				byte[] errdata = new byte[255];
				Reader.READER_ERR err = myapp.Mreader.ParamGet(Reader.Mtr_Param.MTR_PARAM_READER_ERRORDATA, errdata);
				if (err == Reader.READER_ERR.MT_OK_ERR)
				{
					if (errdata[0] == 2) {
						int lostCnt = ((errdata[1] & 0xff) << 8) | (errdata[2] & 0xff);
						Toast.makeText(MainActivity.this,
								"LOST:"+String.valueOf(lostCnt),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else if (myapp.ThreadMODE == 1) {
			if (myapp.Mreader.StopReading() != READER_ERR.MT_OK_ERR) {
				myapp.Mreader.GetLastDetailError(myapp.ei);
				Toast.makeText(MainActivity.this,
						MyApplication.Constr_nostopspreadfailed,
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		/*
		 * if (myapp.Rpower.GetType() == PDATYPE.SCAN_ALPS_ANDROID_CUIUS2) { try
		 * { Thread.sleep(500); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */

		if (myapp.VisTestFor) {
			if (Testcount >= myapp.Vtestforcount) {
				dlog.toDlog("test count reach");
				isrun = false;
			}
		} else { // dlog.toDlog("test count 1 reach");
			isrun = false;
		}

		if (isfinal)
			isrun = false;

		Awl.ReleaseWakeLock();

		myapp.TagsMap.putAll(TagsMap);

		// dlog.toDlog("stop:"+String.valueOf(TagsMap.size()));

		if ((myapp.VisStatics||myapp.isFoundTag) && isrun == false) { // vstatichandler.removeCallbacks(runnable_statics);
			try {
				staticthread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (myapp.isEpcup && isrun == false) {
			SortbyEpc();
		}

		// 时间数量报表
		if (myapp.isReport_rec) {
			Vallcount += totalcount;
			Valltime += runtime;
			/*
			 * 导出txt文件 strlog +="  	"+String.valueOf(Testcount)+ "   ,"; String
			 * temp
			 * =Scount+"/"+VstaticTags.size()+"/"+newadd+"/"+String.valueOf(TagsMap
			 * .size()); temp=String.format("%-12s", temp); strlog+="  	"+temp+
			 * "   ,"; //strlog+=tv_tags.getText().toString();
			 * strlog+="   "+String.valueOf(readtime)+"   ,\r\n\r\n";
			 * 
			 * try { myapp.fs.write(strlog.getBytes()); } catch (IOException e1)
			 * { // TODO Auto-generated catch block e1.printStackTrace(); } //
			 */
			// 导出xls文件

			{
				if (Scount != 0 || VstaticTags.size() != 0) {
					try {
						dexel.WriteExcelfile(new Object[] {
								(Integer) (Testcount), (Integer) Scount,
								(Integer) VstaticTags.size(),
								(Integer) totalcount - totalcountlast,
								(Integer) totalcount, (Long) runtime });

					} catch (WriteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			dexel.Addline(2);

			VstaticTags.clear();
			Scount = 0;
			totalcountlast = 0;
			totalcount = 0;

			if (!myapp.VisTestFor
					|| (myapp.VisTestFor && Testcount >= myapp.Vtestforcount)) {
				String linestr = myapp.myhd.module.toString() + "   ";
				int cnp = Sub4TabActivity.nowpower;
				linestr += String.valueOf(cnp);
				int divd = myapp.Vtestforcount == 0 ? 1 : myapp.Vtestforcount;
				linestr += "  平均标签数量：" + String.valueOf((int) Vallcount / divd)
						+ "\r\n平均耗时：" + String.valueOf((int) Valltime / divd)
						+ "  \r\n日期：" + commfun.getcurDate() + "\r\n\r\n\r\n";

				dexel.mergeandtext(0, dexel.GetY(), 5, dexel.GetY(), linestr);

				Vallcount = 0;
				Valltime = 0;
			}
		}

		if (myapp.isReport_tep) {
			int[] val = new int[1];
			val[0] = 0;

			Object[] objs = new Object[3];
			objs[0] = commfun.getcurDate();
			READER_ERR ert = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_RF_TEMPERATURE, val);
			objs[1] = val[0];
			objs[2] = batt_level;

			try {
				dexel.WriteExcelfile(objs);
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (isrun == false) {
			if (myapp.isReport_rec || myapp.isReport_tep) {
				if (myapp.isReport_tep) {
					mBroadcastReceiver = new MyBroadcastReceiver();
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction(BROADCAST_ACTION1);
					intentFilter.addAction(BROADCAST_ACTION2);
					intentFilter.addAction(BROADCAST_ACTION3);
					registerReceiver(mBroadcastReceiver, intentFilter);

					int[] val = new int[1];
					val[0] = 0;

					Object[] objs = new Object[3];
					objs[0] = commfun.getcurDate();
					READER_ERR ert = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_RF_TEMPERATURE, val);
					objs[1] = val[0];
					objs[2] = batt_level;
				}

				try {
					dexel.SaveandCloseExcelfile();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		if (myapp.isReport_pos) {
			DjxlExcel dexel = new DjxlExcel("标签识读报表");
			try {
				Iterator<Entry<String, TAGINFO>> iesb;
				iesb = myapp.TagsMap.entrySet().iterator();
				List<String> lstr = new ArrayList<String>();
				while (iesb.hasNext()) {
					TAGINFO bd = iesb.next().getValue();
					String linestr = Reader.bytes_Hexstr(bd.EpcId);
					lstr.add(linestr);
				}

				dexel.CreateTestBoxExcelfile_v2(lstr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(MainActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				Toast.makeText(MainActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}

		StopHandleUI();
	}

	private void fcastatic(String epcid) {
		//这里需要用同步锁
		/*
		if (lockobj.tryLock()) {
			try {
				// 处理任务
				Scount++;
				if (!VstaticTags.contains(epcid)) {
					VstaticTags.add(epcid);
				}

			} catch (Exception ex) {

			} finally {
				lockobj.unlock(); // 释放锁
			}
		} else {
			// 如果不能获取锁，则直接做其他事情
		}
		 */
		lockobj.lock();
		try {
			// 处理任务
			Scount++;
			if (!VstaticTags.contains(epcid)) {
				VstaticTags.add(epcid);
			}

		} catch (Exception ex) {

		}
		lockobj.unlock();
	}

	private void fstaticrssicount(String epcid,TAGINFO taf) {

		lockobj.lock();
		try {
			 if(myapp.Curepc.equals(epcid))
			 {
			 	this.Tagdis_isfound=true;
			 	this.Tagdis_all_count+=taf.ReadCnt;
			 	this.Tagdis_all_rssi+=taf.RSSI*taf.ReadCnt;
			 }

		} catch (Exception ex) {

		}
		lockobj.unlock();
	}

	private void emdop_rewriteepc(String epc, String newepc, int opant) {

		TagFilter_ST g2tf = myapp.Mreader.new TagFilter_ST();
		byte[] fdata = new byte[epc.length() / 2];
		myapp.Mreader.Str2Hex(epc, epc.length(), fdata);
		g2tf.fdata = fdata;
		g2tf.flen = epc.length() * 4;
		g2tf.isInvert = 0;
		g2tf.bank = 1;
		g2tf.startaddr = 32;
		myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);

		READER_ERR er = READER_ERR.MT_OK_ERR;
		byte[] data = null;
		data = new byte[newepc.length() / 2];
		myapp.Mreader.Str2Hex(newepc, newepc.length(), data);
		er = myapp.Mreader.WriteTagEpcEx(opant, data, data.length, null,
				(short) myapp.Rparams.optime);

		myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, null);

	}

	/**
	 * 刷新标签缓冲，更新标签列表信息，根据是否u8标签，是否附加数据唯一，天线唯一来列表
	 * 
	 * @param EPC
	 * @param tfs
	 */
	private void TagsBufferResh(String EPC, TAGINFO tfs) {

		if (myapp.VisStatics)
			fcastatic(EPC);

		if(myapp.isFoundTag)
          fstaticrssicount(EPC,tfs);

		String key = EPC;
		String epcstr = EPC;
		String tid = "", bid = "";
		if (myapp.nxpu8 == 1) {
			key = key.substring(0, key.length() - 4);
			bid = epcstr.substring(epcstr.length() - 4, epcstr.length());
			epcstr = epcstr.substring(0, epcstr.length() - 4);
		} else if (myapp.nxpu8 == 2) {
			key = key.substring(0, key.length() - 24);
			tid = epcstr.substring(epcstr.length() - 24, epcstr.length());
			epcstr = epcstr.substring(0, epcstr.length() - 24);
		} else if (myapp.nxpu8 == 3) {
			key = key.substring(0, key.length() - 4);
			bid = epcstr.substring(epcstr.length() - 4, epcstr.length());
			epcstr = epcstr.substring(0, epcstr.length() - 4);
		}

		if (epcstr.length() < 24)
			epcstr = String.format("%-24s", epcstr);

		String emdstr = "";
		String rfu = "";

		if (tfs.EmbededDatalen > 0) {
			char[] out = new char[tfs.EmbededDatalen * 2];
			myapp.Mreader.Hex2Str(tfs.EmbededData, tfs.EmbededDatalen, out);
			emdstr = String.valueOf(out);
			if (myapp.nxpu8 == 1)
				tid = emdstr;
		}

		if (myapp.nxpu8 != 0) {
			char[] out2 = new char[4];
			myapp.Mreader.Hex2Str(tfs.Res, 2, out2);
			rfu = String.valueOf(out2);
		} else {
			// rfu=String.valueOf(((tfs.Res[0]<<8|tfs.Res[1])&0x3f)*180/64);
			rfu = String.valueOf(tfs.Res[1] & 0x3f);
			// rfu=String.valueOf((tfs.Res[1]&0x3f)*180/64);
		}

		if (myapp.isUniByEmd) {
			if (myapp.nxpu8 == 2) {
				key += tid;// nxpu8==2 epcid本身包含tid(nxpu8==2 epcid contain tid)
			} else {
				if (emdstr.isEmpty())
					return;

				if (myapp.nxpu8 != 2)
					key += emdstr;
			}

		}

		if (myapp.isUniByAnt)
			key += String.valueOf(tfs.AntennaID);

		if (!TagsMap.containsKey(key)) {

			/*
			if (locktagsm.tryLock()) {
				TagsMap.put(key, tfs);

				totalcount++;
				locktagsm.unlock();
			}
			 */

			locktagsm.lock();
			TagsMap.put(key, tfs);
			totalcount++;
			locktagsm.unlock();

			// show
			Map<String, String> m = new HashMap<String, String>();
			m.put(Coname[Conidx_sort], String.valueOf(TagsMap.size()));

			if (myapp.isconngoods && myapp.listName.containsKey(epcstr.trim()))
				m.put(Coname[Conidx_epcid], myapp.listName.get(epcstr.trim()));
			else
				m.put(Coname[Conidx_epcid], epcstr);

			m.put(Coname[Conidx_count], String.valueOf(tfs.ReadCnt));
			m.put(Coname[Conidx_ant], String.valueOf(tfs.AntennaID));
			m.put(Coname[Conidx_pro], "");
			m.put(Coname[Conidx_rssi], String.valueOf(tfs.RSSI));
			m.put(Coname[Conidx_fre], String.valueOf(tfs.Frequency));

			m.put(Coname[Conidx_u8tid], tid);
			m.put(Coname[Conidx_u8bid], bid);

			m.put(Coname[Conidx_rfu], rfu);
			m.put(Coname[Conidx_tis], String.valueOf(tfs.TimeStamp));
			if (myapp.nxpu8 == 0)
				m.put(Coname[Conidx_data], emdstr);

			ListMs.add(m);

		} else {

			TAGINFO tf = TagsMap.get(key);

			for (int k = 0; k < ListMs.size(); k++) {
				@SuppressWarnings("unchecked")
				Map<String, String> m = (Map<String, String>) ListMs.get(k);

				boolean bl = false;
				if (myapp.isconngoods
						&& myapp.listName.containsKey(epcstr.trim())) {
					if (m.get(Coname[Conidx_epcid]).equals(
							myapp.listName.get(epcstr.trim()))) {

						if (myapp.isUniByEmd) {
							if (myapp.nxpu8 == 0) {
								if (m.get(Coname[Conidx_data]) == null
										|| m.get(Coname[Conidx_data]).isEmpty()
										|| emdstr.equals(m
												.get(Coname[Conidx_data])))
									bl = true;
							} else {
								if (m.get(Coname[Conidx_u8tid]) == null
										|| m.get(Coname[Conidx_u8tid])
												.isEmpty()
										|| tid.equals(m
												.get(Coname[Conidx_u8tid])))
									bl = true;
							}
						} else
							bl = true;

					}
				} else {
					if (m.get(Coname[Conidx_epcid]).equals(epcstr)) {

						if (myapp.isUniByEmd) {
							if (myapp.nxpu8 == 0) {
								if (m.get(Coname[Conidx_data]) == null
										|| m.get(Coname[Conidx_data]).isEmpty()
										|| emdstr.equals(m
												.get(Coname[Conidx_data])))
									bl = true;
							} else {
								if (m.get(Coname[Conidx_u8tid]) == null
										|| m.get(Coname[Conidx_u8tid])
												.isEmpty()
										|| tid.equals(m
												.get(Coname[Conidx_u8tid])))
									bl = true;
							}

						}
						if (myapp.isUniByAnt) {
							if (String.valueOf(tfs.AntennaID).equals(
									m.get(Coname[3])))
								bl = true;
						} else
							bl = true;

						if (!(myapp.isUniByEmd || myapp.isUniByAnt))
							bl = true;
					}
				}

				if (bl) {
					tf.ReadCnt += tfs.ReadCnt;
					tf.RSSI = tfs.RSSI;
					tf.Frequency = tfs.Frequency;

					m.put(Coname[Conidx_count], String.valueOf(tf.ReadCnt));
					m.put(Coname[Conidx_ant], String.valueOf(tfs.AntennaID));
					m.put(Coname[Conidx_rssi], String.valueOf(tf.RSSI));
					m.put(Coname[Conidx_fre], String.valueOf(tf.Frequency));
					m.put(Coname[Conidx_u8tid], tid);
					m.put(Coname[Conidx_rfu], rfu);
					m.put(Coname[Conidx_tis], String.valueOf(tfs.TimeStamp));
					if (myapp.nxpu8 == 0 && !emdstr.isEmpty())
						m.put(Coname[Conidx_data], emdstr);

					/*
					if (locktagsm.tryLock()) {
						tfs.ReadCnt = tf.ReadCnt;
						TagsMap.put(key, tfs);
						locktagsm.unlock();
					}
					 */
					locktagsm.lock();
					tfs.ReadCnt = tf.ReadCnt;
					TagsMap.put(key, tfs);
					locktagsm.unlock();
					break;
				}
			}
		}
	}

	private void triggerGPO() {

		if (myapp.gpodemo1) {

			myapp.Mreader.SetGPO(1, 1);
		}
		if (myapp.gpodemo2) {
			myapp.Mreader.SetGPO(2, 1);
		}
		if (myapp.gpodemo3) {
			myapp.Mreader.SetGPO(3, 1);
		}
		if (myapp.gpodemo4) {
			myapp.Mreader.SetGPO(4, 1);
		}

		try {
			Thread.sleep(myapp.gpodemotime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (myapp.gpodemo1) {

			myapp.Mreader.SetGPO(1, 0);
		}
		if (myapp.gpodemo2) {
			myapp.Mreader.SetGPO(2, 0);
		}
		if (myapp.gpodemo3) {
			myapp.Mreader.SetGPO(3, 0);
		}
		if (myapp.gpodemo4) {
			myapp.Mreader.SetGPO(4, 0);
		}

	}

	/**
	 * 盘点标签线程
	 */
	private Runnable runnable_MainActivity = new Runnable() {
		public void run() {

			String[] tag = null;
			int[] tagcnt = new int[1];
			tagcnt[0] = 0;
			TelephonyManager tm =(TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
			//String strlmei = tm.getImei();
		//	 String ss =tm.getDeviceId();
		//String ss =  	tm.getSimOperatorName();
			int streadt = 0, enreadt = 0;
			synchronized (this) {
				// Log.d("MYINFO", "read thread....");
				READER_ERR er;
				streadt = (int) System.currentTimeMillis();
				if (myapp.isquicklymode) {
					er = myapp.Mreader.AsyncGetTagCount(tagcnt);
				} else {
					er = myapp.Mreader.TagInventory_Raw(myapp.Rparams.uants,
							myapp.Rparams.uants.length,
							(short) myapp.Rparams.readtime, tagcnt);
				}
				// Log.d("MYINFO","read:" + er.toString() + " cnt:"+
				// String.valueOf(tagcnt[0]));

				if (er == READER_ERR.MT_OK_ERR) {
					if (tagcnt[0] > 0) {
						tv_once.setText(String.valueOf(tagcnt[0]));
						if (myapp.issound)
							soundPool.play(1, 1, 1, 0, 0, 1);
						tag = new String[tagcnt[0]];
						for (int i = 0; i < tagcnt[0]; i++) {
							TAGINFO tfs = myapp.Mreader.new TAGINFO();

							/*
							 * if (myapp.Rpower.GetType()
							 * ==PDATYPE.SCAN_ALPS_ANDROID_CUIUS2) { try {
							 * Thread.sleep(10); } catch (InterruptedException
							 * e) { // TODO Auto-generated catch block
							 * e.printStackTrace(); } }
							 */

							int streadt2 = (int) System.currentTimeMillis();
							if (myapp.isquicklymode)
								er = myapp.Mreader.AsyncGetNextTag(tfs);
							else
								er = myapp.Mreader.GetNextTag(tfs);
							int edreadt2 = (int) System.currentTimeMillis();
							// Log.d("MYINFO", "get tag cost time:"+ (edreadt2 -
							// streadt2));
							// Log.d("MYINFO","get tag index:" +
							// String.valueOf(i)+ " er:" + er.toString());
							if (er == READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {

								tv_state.setText("error:"
										+ String.valueOf(er.value())
										+ er.toString());
								myapp.needreconnect = true;

								StopFunc(true);

							}

							if (er == READER_ERR.MT_OK_ERR) {
								tag[i] = Reader.bytes_Hexstr(tfs.EpcId);

								// 刷新标签缓存(fresh tags buffer)
								TagsBufferResh(tag[i], tfs);

							} else
								break;// 一旦无法从缓冲区获取标签，就要重新调用读标签方法，不能继续获取标签缓冲

							// gpodemo
							if (myapp.gpodemomode == 1) {

								if (Gpodemoauthortags.contains(tag[i])) {

									myapp.Mreader.SetGPO(1, 1);
									myapp.Mreader.SetGPO(2, 1);

									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									myapp.Mreader.SetGPO(1, 0);
									myapp.Mreader.SetGPO(2, 0);

								} else {
									for (int s = 0; s < 3; s++) {
										myapp.Mreader.SetGPO(1, 1);

										try {
											Thread.sleep(20);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										myapp.Mreader.SetGPO(1, 0);

									}

								}
							} else if (myapp.gpodemomode == 2) {

								if (Gpodemoauthortags.contains(tag[i])) {
									myapp.Mreader.SetGPO(1, 1);

									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myapp.Mreader.SetGPO(1, 0);

								} else {
									myapp.Mreader.SetGPO(2, 1);
									try {
										Thread.sleep(300);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myapp.Mreader.SetGPO(2, 0);

								}

							} else if (myapp.gpodemomode == 3) {

								if (Gpodemoauthortags.contains(tag[i])) {
									triggerGPO();
								}

							} else if (myapp.gpodemomode == 4) {

								if (!Gpodemoauthortags.contains(tag[i])) {
									triggerGPO();
								}

							}

						}
						enreadt = (int) System.currentTimeMillis();
						tv_costt.setText("  ct:"
								+ String.valueOf(enreadt - streadt));

					}

				} else {
					myapp.Mreader.GetLastDetailError(myapp.ei);
					tv_state.setText("error:" + String.valueOf(er.value())
							+ " " + er.toString());

					if (myapp.isquicklymode && er != READER_ERR.MT_OK_ERR) {
						if (er != READER_ERR.MT_CMD_FAILED_ERR) {

							StopFunc(true);

							return;
						}
					}

					if (er == READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {

						myapp.needreconnect = true;
						StopFunc(true);

					} else
						handler.postDelayed(this, myapp.Rparams.sleep);
					return;

				}
			}

			if (tag == null) {
				tag = new String[0];
			} else {
				Adapter.notifyDataSetChanged();

			}

			tv_tags.setText(String.valueOf(totalcount));
			long readtime = System.currentTimeMillis() - statenvtick;
			tv_timcost.setText("   rt:" + String.valueOf(readtime));

			handler.postDelayed(this, myapp.Rparams.sleep);

		}
	};

	private Runnable runnable_rssicount_thread = new Runnable() {
		public void run() {

			while (isrun) {
				if(Tagdis_isfound)
				{
					long st=System.currentTimeMillis();
					//do {
						//sound;
						soundPool.play(1, 1, 1, 0, 0, 1);
						try {
							lockobj.lock();
							//double dbv=Math.abs(Tagdis_all_rssi)*1.0/Tagdis_all_count/30;
							//int stim=(int)(Math.pow(10,dbv));

							double dbv=Math.abs(Tagdis_all_rssi)*1.0/Tagdis_all_count;
							int stim=(int)(dbv<=25?10:(dbv-25)*18);
							dlog.toDlog("dbv:"+dbv+" "+"stim---"+String.valueOf(stim));
							lockobj.unlock();

							Thread.sleep(stim);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					//}while(System.currentTimeMillis()-st<Tagdis_soundcycle);
					lockobj.lock();
					Tagdis_isfound=false;
					Tagdis_all_rssi=0;
					Tagdis_all_count=0;
					lockobj.unlock();
				}
				else
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	};


	private Runnable runnable_statics_thread = new Runnable() {
		public void run() {

			while (isrun) {
				// 每秒统计
				// *
				long evetime = System.currentTimeMillis() - statenvtick;

				if (evetime - vstaticstarttick >= staticeveryt) {
					int tagtimes = 0;
					int tagcountunit = 0;
					int tagnew = 0;
					int tagtoal = 0;
					// 时间数量报表
					if (myapp.isReport_rec && evetime < myapp.stoptimems) {

						/*
						if (lockobj.tryLock()) {
							try {

								tagcountunit = VstaticTags.size();
								tagtimes = Scount;
								tagtoal = totalcount;

								tagnew = totalcount - totalcountlast;
								totalcountlast = totalcount;

								Scount = 0;
								VstaticTags.clear();
							} catch (Exception ex) {

							} finally {
								lockobj.unlock(); // 释放锁
							}
						}
						 */
						lockobj.lock();
						tagcountunit = VstaticTags.size();
						tagtimes = Scount;
						tagtoal = totalcount;

						tagnew = totalcount - totalcountlast;
						totalcountlast = totalcount;

						Scount = 0;
						VstaticTags.clear();
						lockobj.unlock();

						try {
							// 处理任务
							/*
							 * 导出txt文件 strlog +="  	    ,"; String
							 * temp=Scount
							 * +"/"+tagcountunit+"/"+newadd+"/"+String
							 * .valueOf(TagsMap.size());
							 * temp=String.format("%-12s", temp); strlog +=
							 * "  	"+temp + "   ,";
							 * strlog+="   "+String.valueOf
							 * (readtime)+"   ,\r\n";
							 */

							// 导出xls文件
							dexel.WriteExcelfile(new Object[]{
									(Integer) (Testcount + 1),
									(Integer) tagtimes,
									(Integer) tagcountunit,
									(Integer) tagnew, (Integer) tagtoal,
									(Long) evetime});

						} catch (Exception ex) {

						}

					}

					// 标签总次数 平均次数
					Iterator<Entry<String, TAGINFO>> iesb;
					/*
					if (locktagsm.tryLock()) {
						iesb = TagsMap.entrySet().iterator();

						while (iesb.hasNext()) {
							TAGINFO bd = iesb.next().getValue();
							allrdcnt += bd.ReadCnt;
						}
						locktagsm.unlock();
					}
					*/
					locktagsm.lock();
					iesb = TagsMap.entrySet().iterator();
					while (iesb.hasNext()) {
						TAGINFO bd = iesb.next().getValue();
						allrdcnt += bd.ReadCnt;
					}
					locktagsm.unlock();

					if (totalcount > 0)
						avgcnt = allrdcnt / totalcount;

					if (tagcountunit > vmaxstaticcount)
						vmaxstaticcount = tagcountunit;
					String text = " a:" + String.valueOf(tagcountunit)
							+ "/s m:" + String.valueOf(vmaxstaticcount) + "/s ";

					// tv_statics.setText(text);
					Message msg = new Message();
					msg.what = 4;
					Bundle data = new Bundle();

					data.putString("Msg_sec", String.valueOf(text));
					msg.setData(data);
					// 发送消息到Handler
					handler2.sendMessage(msg);

					avgcnt = allrdcnt = 0;
					vstaticstarttick = evetime;
				}
				// */
				runtime = System.currentTimeMillis() - statenvtick;
				if ((myapp.isstoptime && runtime >= myapp.stoptimems)
						|| (myapp.isstopcount && totalcount >= myapp.stopcount)) {
					Testcount++;
					Message msg_stop = new Message();
					msg_stop.what = 1;
					handler2.sendMessage(msg_stop);

					if (myapp.VisTestFor) {
						if (Testcount <= myapp.Vtestforcount) {

							// 标签总次数 平均次数
							Iterator<Entry<String, TAGINFO>> iesb;
/*
							if (locktagsm.tryLock()) {
								iesb = TagsMap.entrySet().iterator();
								while (iesb.hasNext()) {
									TAGINFO bd = iesb.next().getValue();
									allrdcnt += bd.ReadCnt;
								}
								locktagsm.unlock();
							}
 */
							locktagsm.lock();
							iesb = TagsMap.entrySet().iterator();
							while (iesb.hasNext()) {
								TAGINFO bd = iesb.next().getValue();
								allrdcnt += bd.ReadCnt;
							}
							locktagsm.unlock();

							if (totalcount > 0)
								avgcnt = allrdcnt / totalcount;

							// dlog.toDlog("static thread sleep");
							try {
								if (isrun)
									Thread.sleep(myapp.Vtestforsleep);
							} catch (InterruptedException e) {
							}

							if (Testcount < myapp.Vtestforcount) {
								TagsMap.clear();
								totalcount = 0;
								totalcountlast = 0;
							}

							if (isrun) {
								if (myapp.isquicklymode) {
									// dlog.toDlog("static  quick read again");
									READER_ERR er = myapp.Mreader
											.AsyncStartReading(
													myapp.Rparams.uants,
													myapp.Rparams.uants.length,
													myapp.Rparams.option);
									if (er != READER_ERR.MT_OK_ERR) {
										Message msg_exstop = new Message();
										msg_exstop.what = 1;
										handler2.sendMessage(msg_exstop);
										return;
									}
								}
								// dlog.toDlog("static thread  again");
								Message msg_start = new Message();
								msg_start.what = 5;
								handler2.sendMessage(msg_start);
								statenvtick = System.currentTimeMillis();
								vstaticstarttick = System.currentTimeMillis()
										- statenvtick;
							}

						}
					} else
						return;
				} else
					try {
						Thread.sleep(myapp.Rparams.sleep);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_lasterr) {
			if (myapp.haveinit == false) {
				Toast.makeText(MainActivity.this,
						MyApplication.Constr_connectfialed, Toast.LENGTH_SHORT)
						.show();
				return false;
			}

			// ErrInfo ei = new ErrInfo();

			if (myapp.ei.errstr == null)
				myapp.Mreader.GetLastDetailError(myapp.ei);

			byte[] errdata = new byte[255];
			READER_ERR er = myapp.Mreader.ParamGet(
					Mtr_Param.MTR_PARAM_READER_ERRORDATA, errdata);

			if (myapp.ei.errstr != null && !myapp.ei.errstr.isEmpty())
				Toast.makeText(
						MainActivity.this,
						"last:" + String.valueOf(myapp.ei.derrcode) + " "
								+ myapp.ei.errstr + " errdata:" + errdata[0]
								+ " " + errdata[1], Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(
						MainActivity.this,
						"last:" + String.valueOf(myapp.ei.derrcode)
								+ " errdata:" + errdata[0] + " " + errdata[1],
						Toast.LENGTH_SHORT).show();

			myapp.ei.errstr = null;
			myapp.ei.derrcode = 0;
		}
		if (!button_read.isEnabled()) {
			Toast.makeText(MainActivity.this, MyApplication.Constr_stopscan,
					Toast.LENGTH_SHORT).show();
		} else {

			if (id == R.id.action_system) {

				Intent intent = new Intent(MainActivity.this,
						SubSystemActivity.class);
				startActivityForResult(intent, 0);
				return true;
			} else if (id == R.id.action_serial) {
				/*
				 * if (myapp.haveinit == false) {
				 * Toast.makeText(MainActivity.this,
				 * MyApplication.Constr_connectfialed,
				 * Toast.LENGTH_SHORT).show(); return false; }
				 */
				Intent intent = new Intent(MainActivity.this,
						SubDebugActivity.class);
				startActivityForResult(intent, 0);
				return true;
			} else if (id == R.id.action_qt) {
				if (myapp.haveinit == false) {
					Toast.makeText(MainActivity.this,
							MyApplication.Constr_connectfialed,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				Intent intent = new Intent(MainActivity.this,
						SubQTActivity.class);
				startActivityForResult(intent, 0);
				return true;
			} else if (id == R.id.action_cw) {
				if (myapp.haveinit == false) {
					Toast.makeText(MainActivity.this,
							MyApplication.Constr_connectfialed,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				Intent intent = new Intent(MainActivity.this,
						SubCarryWaveActivity.class);
				startActivityForResult(intent, 0);
			} else if (id == R.id.action_reg) {
				if (myapp.haveinit == false) {
					Toast.makeText(MainActivity.this,
							MyApplication.Constr_connectfialed,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				if (right_reg == false) {
					Toast.makeText(MainActivity.this, "no permissions",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				Intent intent = new Intent(MainActivity.this,
						SubRegopActivity.class);
				startActivityForResult(intent, 0);
			} else if (id == R.id.action_tagttl) {
				if (myapp.haveinit == false) {
					Toast.makeText(MainActivity.this,
							MyApplication.Constr_connectfialed,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				Intent intent = new Intent(MainActivity.this,
						SubTagTempLedActivity.class);
				startActivityForResult(intent, 0);
			} else if (id == R.id.action_update) {

				if (myapp.Address == null || myapp.Address.isEmpty())
					myapp.Address = ((EditText) findViewById(R.id.editText_adr))
							.getText().toString();
				Toast.makeText(MainActivity.this, "请确保已单击连接按钮/Connect",
						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(MainActivity.this,
						UpdateActivity.class);
				startActivityForResult(intent, 0);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	private void ReadHandleUI() {
		this.button_read.setEnabled(false);
		this.button_stop.setEnabled(true);
		TabWidget tw = myapp.tabHost.getTabWidget();
		if (RULE_NOSELPT) {
			tw.getChildAt(1).setEnabled(false);
			tw.getChildAt(2).setEnabled(false);
		} else {
			tw.getChildAt(0).setEnabled(false);
			tw.getChildAt(2).setEnabled(false);
			tw.getChildAt(3).setEnabled(false);
		}
	}

	private void StopHandleUI() {
		button_read.setEnabled(true);
		button_stop.setEnabled(false);
		TabWidget tw = myapp.tabHost.getTabWidget();
		if (RULE_NOSELPT)  {
			tw.getChildAt(1).setEnabled(true);
			tw.getChildAt(2).setEnabled(true);
		} else {
			tw.getChildAt(0).setEnabled(true);
			tw.getChildAt(2).setEnabled(true);
			tw.getChildAt(3).setEnabled(true);
		}
	}

	@Override
	public void onResume() {

		super.onResume();

	}

	@Override
	protected void onDestroy() {
		//Awl.ReleaseWakeLock();
		unregisterReceiver(mBroadcastReceiver);

		if (myapp.isReport_rec) {
			try {
				if (myapp.fs != null)
					myapp.fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.exit(0);
		super.onDestroy();
		Intent pintent = new Intent("android.intent.action.SETTINGS_BJ");
		pintent.putExtra( "enable" , false);
		getApplicationContext().sendBroadcast(pintent);
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	/**
	 * 按钮控制盘点开始以及停止
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
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
				if (handler != null)
					handler.removeCallbacks(runnable_MainActivity);
				if (myapp.Mreader != null) {
					myapp.Mreader.CloseReader();
					myapp.needlisen = true;
				}
                //不知道什么意思， 报错了
				//myapp.Rpower.PowerDown();
				finish();
				// System.exit(0);
				Intent pintent = new Intent("android.intent.action.SETTINGS_BJ");
				pintent.putExtra( "enable" , false);
				getApplicationContext().sendBroadcast(pintent);
			}
			return true;
		} else if (keyCode == 139) {
//		} else if (keyCode == 139 && myapp.Rpower.GetType() == PDATYPE.CHAINWAY) {
			if (button_read.isEnabled()) {
				button_read.performClick();
			} else
				button_stop.performClick();
		}

		return super.onKeyDown(keyCode, event);
	}




    private void ConnectHandleUI1() {
        try {
            READER_ERR er;
            myapp.Rparams = myapp.spf.ReadReaderParams();

            if(isdefaultmaxpow_V)
            {

                int[] mp = new int[1];
                er = myapp.Mreader.ParamGet(
                        Mtr_Param.MTR_PARAM_RF_MAXPOWER, mp);
                if (er == READER_ERR.MT_OK_ERR) {
                    myapp.Rparams.setdefaulpwval((short)mp[0]);

                }

            }

            if (myapp.Rparams.invpro.size() < 1)
                myapp.Rparams.invpro.add("GEN2");

            List<SL_TagProtocol> ltp = new ArrayList<SL_TagProtocol>();
            for (int i = 0; i < myapp.Rparams.invpro.size(); i++) {
                if (myapp.Rparams.invpro.get(i).equals("GEN2")) {
                    ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);

                } else if (myapp.Rparams.invpro.get(i).equals("6B")) {
                    ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);

                } else if (myapp.Rparams.invpro.get(i).equals("IPX64")) {
                    ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);

                } else if (myapp.Rparams.invpro.get(i).equals("IPX256")) {
                    ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);

                }
            }

            Inv_Potls_ST ipst = myapp.Mreader.new Inv_Potls_ST();
            ipst.potlcnt = ltp.size();
            ipst.potls = new Inv_Potl[ipst.potlcnt];
            SL_TagProtocol[] stp = ltp
                    .toArray(new SL_TagProtocol[ipst.potlcnt]);
            for (int i = 0; i < ipst.potlcnt; i++) {
                Inv_Potl ipl = myapp.Mreader.new Inv_Potl();
                ipl.weight = 30;
                ipl.potl = stp[i];
                ipst.potls[i] = ipl;
            }

            er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
            Log.d("MYINFO", "Connected set pro:" + er.toString());

            er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
                    new int[] { myapp.Rparams.checkant });
            Log.d("MYINFO", "Connected set checkant:" + er.toString());

            AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
            apcf.antcnt = myapp.antportc;
            for (int i = 0; i < apcf.antcnt; i++) {
                AntPower jaap = myapp.Mreader.new AntPower();
                jaap.antid = i + 1;
                jaap.readPower = (short) myapp.Rparams.rpow[i];
                jaap.writePower = (short) myapp.Rparams.wpow[i];
                apcf.Powers[i] = jaap;
            }
            Sub4TabActivity.nowpower=myapp.Rparams.rpow[0];
            myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);

            Region_Conf rre;
            switch (myapp.Rparams.region) {
                case 0:
                    rre = Region_Conf.RG_PRC;
                    break;
                case 1:
                    rre = Region_Conf.RG_NA;
                    break;
                case 2:
                    rre = Region_Conf.RG_NONE;
                    break;
                case 3:
                    rre = Region_Conf.RG_KR;
                    break;
                case 4:
                    rre = Region_Conf.RG_EU;
                    break;
                case 5:
                    rre = Region_Conf.RG_EU2;
                    break;
                case 6:
                    rre = Region_Conf.RG_EU3;
                    break;
                case 9:
                    rre=Region_Conf.RG_OPEN;
                    break;
                case 10:
                    rre=Region_Conf.RG_PRC2;
                    break;
                case 7:
                case 8:
                default:
                    rre = Region_Conf.RG_NONE;
                    break;
            }
            if (rre != Region_Conf.RG_NONE) {
                er = myapp.Mreader.ParamSet(
                        Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rre);
            }

            if (myapp.Rparams.frelen > 0) {

                HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
                hdst.lenhtb = myapp.Rparams.frelen;
                hdst.htb = myapp.Rparams.frecys;
                er = myapp.Mreader.ParamSet(
                        Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
            }

            er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
                    new int[] { myapp.Rparams.session });
            er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q,
                    new int[] { myapp.Rparams.qv });
            er = myapp.Mreader.ParamSet(
                    Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE,
                    new int[] { myapp.Rparams.wmode });
            er = myapp.Mreader.ParamSet(
                    Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN,
                    new int[] { myapp.Rparams.maxlen });
            er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET,
                    new int[] { myapp.Rparams.target });

            if (myapp.Rparams.filenable == 1) {
                TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();
                tfst.bank = myapp.Rparams.filbank;
                int len = myapp.Rparams.fildata.length();
                len = len % 2 == 0 ? len : len + 1;
                tfst.fdata = new byte[len / 2];
                myapp.Mreader.Str2Hex(myapp.Rparams.fildata,
                        myapp.Rparams.fildata.length(), tfst.fdata);
                tfst.flen = myapp.Rparams.fildata.length() * 4;
                tfst.startaddr = myapp.Rparams.filadr;
                tfst.isInvert = myapp.Rparams.filisinver;

                myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
            }

            if (myapp.Rparams.emdenable == 1) {
                EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();

                edst.accesspwd = null;
                edst.bank = myapp.Rparams.emdbank;
                edst.startaddr = myapp.Rparams.emdadr;
                edst.bytecnt = myapp.Rparams.emdbytec;

                er = myapp.Mreader.ParamSet(
                        Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
            }

            er = myapp.Mreader.ParamSet(
                    Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA,
                    new int[] { myapp.Rparams.adataq });
            er = myapp.Mreader.ParamSet(
                    Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI,
                    new int[] { myapp.Rparams.rhssi });
            er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE,
                    new int[] { myapp.Rparams.invw });

            Reader.AntPortsVSWR apvr=myapp.Mreader.new AntPortsVSWR();
            apvr.andid=1;
            apvr.power=(short) myapp.Rparams.rpow[0];
            apvr.region=Region_Conf.RG_NA;
            er=myapp.Mreader.ParamGet(Mtr_Param.MTR_PARAM_RF_ANTPORTS_VSWR, apvr);

            TextView tv_module = (TextView) findViewById(R.id.textView_module);

            HardwareDetails val = myapp.Mreader.new HardwareDetails();
            er = myapp.Mreader.GetHardwareDetails(val);

            if (er == READER_ERR.MT_OK_ERR) {

                tv_module.setText("module:"+val.module.toString()+"\r\nhard:"+
                        myapp.dv.hardwareVer+"\r\nsoft:"+myapp.dv.softwareVer);
                myapp.myhd=val;
            }
			/*
			long st=System.currentTimeMillis();
			byte[] restmodule=new byte[]{(byte) 0xff,0x0f,(byte) 0xaa,0x4d,0x6f,0x64,0x75,
					0x6c,0x65,0x74,0x65,0x63,0x68,(byte) 0xaa,0x4b,0x00,(byte) 0xf5,(byte) 0xbb,(byte) 0xf8,0x3a};
			int re=myapp.Mreader.DataTransportSend(restmodule,restmodule.length,500);

			byte[] revd=new byte[19];
			re=myapp.Mreader.DataTransportRecv(revd,19,1000);
			Log.d("MYINFO", "reset cost:"+String.valueOf(System.currentTimeMillis()-st));
            //*/
        } catch (Exception ex) {
            Log.d("MYINFO",
                    ex.getMessage() + ex.toString() + ex.getStackTrace());
        }
/*        this.button_connect.setEnabled(false);
        this.button_disconnect.setEnabled(true);*/
        TabWidget tw = myapp.tabHost.getTabWidget();
        tw.getChildAt(1).setVisibility(View.VISIBLE);
        tw.getChildAt(2).setVisibility(View.VISIBLE);
        tw.getChildAt(3).setVisibility(View.VISIBLE);
        myapp.tabHost.setCurrentTab(1);
    }





	/**
	 * 连接后读取配置文件来配置读写器参数
	 */
	private void ConnectHandleUI() {
		try {
			READER_ERR er;
			myapp.Rparams = myapp.spf.ReadReaderParams();

			if (myapp.Rparams.invpro.size() < 1)
				myapp.Rparams.invpro.add("GEN2");

			List<SL_TagProtocol> ltp = new ArrayList<SL_TagProtocol>();
			for (int i = 0; i < myapp.Rparams.invpro.size(); i++) {
				if (myapp.Rparams.invpro.get(i).equals("GEN2")) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);

				} else if (myapp.Rparams.invpro.get(i).equals("6B")) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);

				} else if (myapp.Rparams.invpro.get(i).equals("IPX64")) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);

				} else if (myapp.Rparams.invpro.get(i).equals("IPX256")) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);

				}
			}

			Inv_Potls_ST ipst = myapp.Mreader.new Inv_Potls_ST();
			ipst.potlcnt = ltp.size();
			ipst.potls = new Inv_Potl[ipst.potlcnt];
			SL_TagProtocol[] stp = ltp
					.toArray(new SL_TagProtocol[ipst.potlcnt]);
			for (int i = 0; i < ipst.potlcnt; i++) {
				Inv_Potl ipl = myapp.Mreader.new Inv_Potl();
				ipl.weight = 30;
				ipl.potl = stp[i];
				ipst.potls[0] = ipl;
			}

			er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
			Log.d("MYINFO", "Connected set pro:" + er.toString());

			er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
					new int[] { myapp.Rparams.checkant });
			Log.d("MYINFO", "Connected set checkant:" + er.toString());

			AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
			apcf.antcnt = myapp.antportc;
			for (int i = 0; i < apcf.antcnt; i++) {
				AntPower jaap = myapp.Mreader.new AntPower();
				jaap.antid = i + 1;
				jaap.readPower = (short) myapp.Rparams.rpow[i];
				jaap.writePower = (short) myapp.Rparams.wpow[i];
				apcf.Powers[i] = jaap;
			}

			myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);

			Region_Conf rre;
			switch (myapp.Rparams.region) {
			case 0:
				rre = Region_Conf.RG_PRC;
				break;
			case 1:
				rre = Region_Conf.RG_NA;
				break;
			case 2:
				rre = Region_Conf.RG_NONE;
				break;
			case 3:
				rre = Region_Conf.RG_KR;
				break;
			case 4:
				rre = Region_Conf.RG_EU;
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			default:
				rre = Region_Conf.RG_NONE;
				break;
			}
			if (rre != Region_Conf.RG_NONE) {
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rre);
			}

			if (myapp.Rparams.frelen > 0) {

				HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
				hdst.lenhtb = myapp.Rparams.frelen;
				hdst.htb = myapp.Rparams.frecys;
				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
			}

			er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
					new int[] { myapp.Rparams.session });
			er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q,
					new int[] { myapp.Rparams.qv });
			er = myapp.Mreader.ParamSet(
					Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE,
					new int[] { myapp.Rparams.wmode });
			er = myapp.Mreader.ParamSet(
					Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN,
					new int[] { myapp.Rparams.maxlen });
			er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET,
					new int[] { myapp.Rparams.target });

			if (myapp.Rparams.filenable == 1) {
				TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();
				tfst.bank = myapp.Rparams.filbank;
				int len = myapp.Rparams.fildata.length();
				len = len % 2 == 0 ? len : len + 1;
				tfst.fdata = new byte[len / 2];
				myapp.Mreader.Str2Hex(myapp.Rparams.fildata,
						myapp.Rparams.fildata.length(), tfst.fdata);
				tfst.flen = myapp.Rparams.fildata.length() * 4;
				tfst.startaddr = myapp.Rparams.filadr;
				tfst.isInvert = myapp.Rparams.filisinver;

				myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
			}

			if (myapp.Rparams.emdenable == 1) {
				EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();

				edst.accesspwd = null;
				edst.bank = myapp.Rparams.emdbank;
				edst.startaddr = myapp.Rparams.emdadr;
				edst.bytecnt = myapp.Rparams.emdbytec;
				edst.accesspwd = null;

				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
			}

			er = myapp.Mreader.ParamSet(
					Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA,
					new int[] { myapp.Rparams.adataq });
			er = myapp.Mreader.ParamSet(
					Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI,
					new int[] { myapp.Rparams.rhssi });
			er = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE,
					new int[] { myapp.Rparams.invw });
			/*
			 * AntPortsVSWR apvr=myapp.Mreader.new AntPortsVSWR(); apvr.andid=1;
			 * apvr.power=(short) myapp.Rparams.rpow[0];
			 * apvr.region=Region_Conf.RG_NA;
			 * er=myapp.Mreader.ParamGet(Mtr_Param.MTR_PARAM_RF_ANTPORTS_VSWR,
			 * apvr);
			 */

			TextView tv_module = (TextView) findViewById(R.id.textView_module);
			HardwareDetails val = myapp.Mreader.new HardwareDetails();
			er = myapp.Mreader.GetHardwareDetails(val);
			if (er == READER_ERR.MT_OK_ERR) {
				tv_module.setText(val.module.toString());
			}

		} catch (Exception ex) {
			Log.d("MYINFO",
					ex.getMessage() + ex.toString() + ex.getStackTrace());
		}

	}

	/**
	 * 根据android 平台设置显示语言
	 */
	private void setLange() {
		// Toast.makeText(getApplicationContext(), "setl1",
		// Toast.LENGTH_SHORT).show();
		Locale locale = getApplicationContext().getResources()
				.getConfiguration().locale;
		String language = locale.getLanguage();
		MyApplication.Constr_READ = this.getString(R.string.Constr_READ);
		MyApplication.Constr_CONNECT = this.getString(R.string.Constr_CONNECT);
		MyApplication.Constr_INVENTORY = this
				.getString(R.string.Constr_INVENTORY);
		MyApplication.Constr_RWLOP = this.getString(R.string.Constr_RWLOP);
		MyApplication.Constr_set = this.getString(R.string.Constr_set);
		MyApplication.Constr_SetFaill = this
				.getString(R.string.Constr_SetFaill);
		MyApplication.Constr_GetFaill = this
				.getString(R.string.Constr_GetFaill);
		MyApplication.Constr_SetOk = this.getString(R.string.Constr_SetOk);
		MyApplication.Constr_unsupport = this
				.getString(R.string.Constr_unsupport);
		MyApplication.Constr_Putandexit = this
				.getString(R.string.Constr_Putandexit);

		MyApplication.Constr_stopscan = this
				.getString(R.string.Constr_stopscan);
		MyApplication.Constr_hadconnected = this
				.getString(R.string.Constr_hadconnected);
		MyApplication.Constr_plsetuuid = this
				.getString(R.string.Constr_plsetuuid);
		MyApplication.Constr_pwderror = this
				.getString(R.string.Constr_pwderror);
		MyApplication.Constr_search = this.getString(R.string.Constr_search);
		MyApplication.Constr_stop = this.getString(R.string.Constr_stop);

		MyApplication.Constr_createreaderok = this
				.getString(R.string.Constr_createreaderok);

		MyApplication.Constr_sub3readmem = this
				.getString(R.string.Constr_sub3readmem);
		MyApplication.Constr_sub3writemem = this
				.getString(R.string.Constr_sub3writemem);
		MyApplication.Constr_sub3lockkill = this
				.getString(R.string.Constr_sub3lockkill);
		MyApplication.Constr_sub3readfail = this
				.getString(R.string.Constr_sub3readfail);
		MyApplication.Constr_sub3nodata = this
				.getString(R.string.Constr_sub3nodata);
		MyApplication.Constr_sub3wrtieok = this
				.getString(R.string.Constr_sub3wrtieok);
		MyApplication.Constr_sub3writefail = this
				.getString(R.string.Constr_sub3writefail);
		MyApplication.Constr_sub3lockok = this
				.getString(R.string.Constr_sub3lockok);
		MyApplication.Constr_sub3lockfail = this
				.getString(R.string.Constr_sub3lockfail);
		MyApplication.Constr_sub3killok = this
				.getString(R.string.Constr_sub3killok);
		MyApplication.Constr_sub3killfial = this
				.getString(R.string.Constr_sub3killfial);

		MyApplication.Auto = this.getString(R.string.Auto);
		MyApplication.No = this.getString(R.string.No);
		MyApplication.Constr_sub4invenpra = this
				.getString(R.string.Constr_sub4invenpra);
		MyApplication.Constr_sub4antpow = this
				.getString(R.string.Constr_sub4antpow);
		MyApplication.Constr_sub4regionfre = this
				.getString(R.string.Constr_sub4regionfre);
		MyApplication.Constr_sub4gen2opt = this
				.getString(R.string.Constr_sub4gen2opt);
		MyApplication.Constr_sub4invenfil = this
				.getString(R.string.Constr_sub4invenfil);
		MyApplication.Constr_sub4addidata = this
				.getString(R.string.Constr_sub4addidata);
		MyApplication.Constr_sub4others = this
				.getString(R.string.Constr_sub4others);
		MyApplication.Constr_sub4quickly = this
				.getString(R.string.Constr_sub4quickly);
		MyApplication.Constr_sub4setmodefail = this
				.getString(R.string.Constr_sub4setmodefail);
		MyApplication.Constr_sub4setokresettoab = this
				.getString(R.string.Constr_sub4setokresettoab);
		MyApplication.Constr_sub4ndsapow = this
				.getString(R.string.Constr_sub4ndsapow);
		MyApplication.Constr_sub4unspreg = this
				.getString(R.string.Constr_sub4unspreg);

		MyApplication.Constr_subblmode = this
				.getString(R.string.Constr_subblmode);
		MyApplication.Constr_subblinven = this
				.getString(R.string.Constr_subblinven);
		MyApplication.Constr_subblfil = this
				.getString(R.string.Constr_subblfil);
		MyApplication.Constr_subblfre = this
				.getString(R.string.Constr_subblfre);
		MyApplication.Constr_subblnofre = this
				.getString(R.string.Constr_subblnofre);

		MyApplication.Constr_subcsalterpwd = this
				.getString(R.string.Constr_subcsalterpwd);
		MyApplication.Constr_subcslockwpwd = this
				.getString(R.string.Constr_subcslockwpwd);
		MyApplication.Constr_subcslockwoutpwd = this
				.getString(R.string.Constr_subcslockwoutpwd);
		MyApplication.Constr_subcsplsetimeou = this
				.getString(R.string.Constr_subcsplsetimeou);
		MyApplication.Constr_subcsputcnpwd = this
				.getString(R.string.Constr_subcsputcnpwd);
		MyApplication.Constr_subcsplselreg = this
				.getString(R.string.Constr_subcsplselreg);
		MyApplication.Constr_subcsopfail = this
				.getString(R.string.Constr_subcsopfail);
		MyApplication.Constr_subcsputcurpwd = this
				.getString(R.string.Constr_subcsputcurpwd);

		MyApplication.Constr_subdbdisconnreconn = this
				.getString(R.string.Constr_subdbdisconnreconn);
		MyApplication.Constr_subdbhadconnected = this
				.getString(R.string.Constr_subdbhadconnected);
		MyApplication.Constr_subdbconnecting = this
				.getString(R.string.Constr_subdbconnecting);
		MyApplication.Constr_subdbrev = this
				.getString(R.string.Constr_subdbrev);
		MyApplication.Constr_subdbstop = this
				.getString(R.string.Constr_subdbstop);
		MyApplication.Constr_subdbdalennot = this
				.getString(R.string.Constr_subdbdalennot);
		MyApplication.Constr_subdbplpuhexchar = this
				.getString(R.string.Constr_subdbplpuhexchar);

		MyApplication.Constr_subsysaveok = this
				.getString(R.string.Constr_subsysaveok);
		MyApplication.Constr_subsysout = this
				.getString(R.string.Constr_subsysout);
		MyApplication.Constr_subsysreavaid = this
				.getString(R.string.Constr_subsysreavaid);
		MyApplication.Constr_sub1recfailed = this
				.getString(R.string.Constr_sub1recfailed);
		MyApplication.Constr_subsysavefailed = this
				.getString(R.string.Constr_subsysavefailed);
		MyApplication.Constr_subsysexefin = this
				.getString(R.string.Constr_subsysexefin);
		MyApplication.Constr_sub1adrno = this
				.getString(R.string.Constr_sub1adrno);
		MyApplication.Constr_sub1pdtsl = this
				.getString(R.string.Constr_sub1pdtsl);
		MyApplication.Constr_mainpu = this.getString(R.string.Constr_mainpu);
		MyApplication.Constr_nostopstreadfailed = this
				.getString(R.string.Constr_nostopstreadfailed);
		MyApplication.Constr_nostopspreadfailed = this
				.getString(R.string.Constr_nostopspreadfailed);
		MyApplication.Constr_nostopreadfailed = this
				.getString(R.string.Constr_nostopreadfailed);
		MyApplication.Constr_connectok = this
				.getString(R.string.Constr_connectok);
		MyApplication.Constr_connectfialed = this
				.getString(R.string.Constr_connectfialed);
		MyApplication.Constr_disconpowdown = this
				.getString(R.string.Constr_disconpowdown);
		MyApplication.Constr_ok = this.getString(R.string.Constr_ok);
		MyApplication.Constr_failed = this.getString(R.string.Constr_failed);
		MyApplication.Constr_excep = this.getString(R.string.Constr_excep);
		MyApplication.Constr_setcep = this.getString(R.string.Constr_setcep);
		MyApplication.Constr_getcep = this.getString(R.string.Constr_getcep);
		MyApplication.Constr_killok = this.getString(R.string.Constr_killok);
		MyApplication.Constr_killfailed = this
				.getString(R.string.Constr_killfailed);
		MyApplication.Constr_psiant = this.getString(R.string.Constr_psiant);
		MyApplication.Constr_selpro = this.getString(R.string.Constr_selpro);

		MyApplication.Constr_carry_fftable = this
				.getString(R.string.Constr_carry_fftable);
		MyApplication.Constr_carry_frtable = this
				.getString(R.string.Constr_carry_frtable);
		MyApplication.Constr_carry_binvpw = this
				.getString(R.string.Constr_carry_binvpw);
		MyApplication.Constr_carry_binvfpw = this
				.getString(R.string.Constr_carry_binvfpw);
		MyApplication.Constr_carry_invc = this
				.getString(R.string.Constr_carry_invc);
		MyApplication.Constr_carry_invtep = this
				.getString(R.string.Constr_carry_invtep);
		MyApplication.Constr_carry_invpw = this
				.getString(R.string.Constr_carry_invpw);
		MyApplication.Constr_carry_ainvfpw = this
				.getString(R.string.Constr_carry_ainvfpw);
		MyApplication.Constr_carry_ainvpw = this
				.getString(R.string.Constr_carry_ainvpw);

		MyApplication.Coname = this.getResources().getStringArray(
				R.array.Coname);

		MyApplication.pdaatpot = this.getResources().getStringArray(
				R.array.pdaatpot);

		MyApplication.spibank = this.getResources().getStringArray(
				R.array.spibank);
		MyApplication.spifbank = this.getResources().getStringArray(
				R.array.spifbank);
		MyApplication.spilockbank = this.getResources().getStringArray(
				R.array.spilockbank);
		MyApplication.spilocktype = this.getResources().getStringArray(
				R.array.spilocktype);

		MyApplication.spireg = this.getResources().getStringArray(
				R.array.spireg);
		MyApplication.spinvmo = this.getResources().getStringArray(
				R.array.spinvmo);
		MyApplication.spitari = this.getResources().getStringArray(
				R.array.spitari);
		MyApplication.spiwmod = this.getResources().getStringArray(
				R.array.spiwmod);

		MyApplication.cusreadwrite = this.getResources().getStringArray(
				R.array.cusreadwrite);
		MyApplication.cuslockunlock = this.getResources().getStringArray(
				R.array.cuslockunlock);

		MyApplication.regtype = this.getResources().getStringArray(
				R.array.regtype);

		MyApplication.spiqmode = this.getResources().getStringArray(
				R.array.qmodes);
		MyApplication.gpodemo = this.getResources().getStringArray(
				R.array.gpodemo);

	}

	private void showPopupMenu(View view) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(this, view);

		// menu布局
		popupMenu.getMenuInflater()
				.inflate(R.menu.tagspop, popupMenu.getMenu());
		// menu的item点击事件
		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {

						if (item.getItemId() == R.id.menu_tagop_add) {
							if (!Gpodemoauthortags.contains(myapp.Curepc))
								Gpodemoauthortags.add(myapp.Curepc);

						} else if (item.getItemId() == R.id.menu_tagop_rem) {
							if (Gpodemoauthortags.contains(myapp.Curepc))
								Gpodemoauthortags.remove(myapp.Curepc);
						}

						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						return false;
					}
				});
		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {

			}
		});

		popupMenu.show();
	}

}
