package com.example.module_android_demo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.function.MyAdapter_fre;
import com.function.MyAdapter_mfil;
import com.function.commfun;
import com.function.MyAdapter_fre.ViewHolder_fre;
import com.function.MyAdapter_mfil.ViewHolder_mfil;
import com.tools.dlog;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.R2000_calibration.Region;
import com.uhf.api.cls.Reader.AntPower;
import com.uhf.api.cls.Reader.AntPowerConf;
import com.uhf.api.cls.Reader.ConnAnts_ST;
import com.uhf.api.cls.Reader.CustomCmdType;
import com.uhf.api.cls.Reader.EmbededData_ST;
import com.uhf.api.cls.Reader.HoptableData_ST;
import com.uhf.api.cls.Reader.IT_MODE;
import com.uhf.api.cls.Reader.Inv_Potl;
import com.uhf.api.cls.Reader.Inv_Potls_ST;
import com.uhf.api.cls.Reader.Module_Type;
import com.uhf.api.cls.Reader.Mtr_Param;
import com.uhf.api.cls.Reader.NXP_U8_InventoryModePara;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.Region_Conf;
import com.uhf.api.cls.Reader.SL_TagProtocol;
import com.uhf.api.cls.Reader.TagFilter_ST;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;

/**
 * 参数配置页面
 * 
 * @author Administrator
 * 
 */
public class Sub4TabActivity extends Activity {

	String[] spises = { "S0", "S1", "S2", "S3" };
	String[] spipow = { "100","200","300","400","500", "600", "700", "800", "900", "1000", "1100",
			"1200", "1300", "1400", "1500", "1600", "1700", "1800", "1900",
			"2000", "2100", "2200", "2300", "2400", "2500", "2600", "2700",
			"2800", "2900", "3000", "3100", "3200", "3300" };// 运行时添加(add to
																// when running)
  int minpw=Integer.valueOf(spipow[0]);
	String[] spiq = { "自动", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12", "13", "14", "15" };

	String[] spiblf = { "40", "250", "400", "640" };
	String[] spimlen = { "96", "496" };
	String[] spitget = { "A", "B", "A-B", "B-A" };
	String[] spigcod = { "FM0", "M2", "M4", "M8","PROF0",
            "PROF1",
            "PROF2",
            "PROF3",
            "PROF4",
            "PROF5",
			"RFM_1",
			"RFM_3",
			"RFM_5",
			"RFM_7",
			"RFM_11",
			"RFM_12",
			"RFM_13",
			"RFM_15",

    };

	String[] spi6btzsd = { "99percent", "11percent" };
	String[] spidelm = { "Delimiter1", "Delimiter4" };
	String[] spiperst = { "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%",
			"40%", "45%", "50%" };
	String[] spigpodemo;

	CheckBox cb_gpo1, cb_gpo2, cb_gpo3, cb_gpo4, cb_gpi1, cb_gpi2, cb_gpi3,
			cb_gpi4, cb_oant, cb_odata, cb_hrssi, cb_gen2, cb_6b, cb_ipx64,
			cb_ipx256, cb_ant1, cb_ant2, cb_ant3, cb_ant4, cb_ant5, cb_ant6,
			cb_ant7, cb_ant8, cb_ant9, cb_ant10, cb_ant11, cb_ant12, cb_ant13,
			cb_ant14, cb_ant15, cb_ant16, cb_allsel, cbmf_readcount,
			cbmf_rssi, cbmf_ant, cbmf_fre, cbmf_time, cbmf_rfu, cbmf_pro,
			cbmf_dl, cb_fre, cb_showuinemd,cb_epcup, cb_showuinant,cb_report_rec,
			cb_report_pos,cb_report_tep,cb_fastid,cb_tagfoucs,cb_tagfound;
	RadioGroup rg_emdenable, rg_antcheckenable, rg_invfilenable,
			rg_invfilmatch, rg_nxpu8;

	private ArrayAdapter<String> arrdp_bank, arrdp_fbank, arrdp_ses,
			arradp_pow, arrdp_q, arrdp_invmo, arrdp_blf, arrdp_mlen,
			arrdp_tget, arrdp_g2cod, arrdp_tari, arrdp_wmod, arrdp_6btzsd,
			arrdp_delm, arradp_reg, arrdp_per,arrdp_qmode,arrdp_gpodemo;
	Spinner spinner_ant1rpow, spinner_ant1wpow, spinner_ant2rpow,
			spinner_ant2wpow, spinner_ant3rpow, spinner_ant3wpow,
			spinner_ant4rpow, spinner_ant4wpow, spinner_ant5rpow,
			spinner_ant5wpow, spinner_ant6rpow, spinner_ant6wpow,
			spinner_ant7rpow, spinner_ant7wpow, spinner_ant8rpow,
			spinner_ant8wpow, spinner_ant9rpow, spinner_ant9wpow,
			spinner_ant10rpow, spinner_ant10wpow, spinner_ant11rpow,
			spinner_ant11wpow, spinner_ant12rpow, spinner_ant12wpow,
			spinner_ant13rpow, spinner_ant13wpow, spinner_ant14rpow,
			spinner_ant14wpow, spinner_ant15rpow, spinner_ant15wpow,
			spinner_ant16rpow, spinner_ant16wpow, spinner_sesion, spinner_q,
			spinner_wmode, spinner_blf, spinner_maxlen, spinner_target,
			spinner_g2code, spinner_tari, spinner_emdbank, spinner_filbank,
			spinner_region, spinner_invmode, spinner_6btzsd, spinner_delmi,
			spinner_persen,spinner_gpodemo;//,spinner_qmode;
	public static Spinner spinner_qmode;
	TabHost tabHost_set;

	Button button_getantpower, button_setantpower, button_getantcheck,
			button_setantcheck, button_getgen2ses, button_setgen2ses,
			button_getgen2q, button_setgen2q, button_getwmode, button_setwmode,
			button_getgen2blf, button_setgenblf, button_getgen2maxl,
			button_setgen2maxl, button_getgen2targ, button_setgen2targ,
			button_getgen2code, button_setgen2code, button_getgen2tari,
			button_setgen2tari, button_setgpo, button_getgpi, button_getemd,
			button_setemd, button_getfil, button_setfil, button_getmfil, button_setmfil,
			button_getreg, button_setreg, button_getfre, button_setfre, button_getusl,
			button_setusl, button_invproset, button_opproget, button_opproset,
			button_invantsset, button_invantsget,

			button_oantuqget, button_oantuqset, button_odatauqget,
			button_odatauqset, button_hrssiget, button_hrssiset,
			button_invmodeget, button_invmodeset, button_6bdpget,
			button_6bdpset, button_6bdltget, button_6bdltset, button_6bblfget,
			button_6bblfset, button_gettempture, button_quicklyread,button_nxpu8,
			button_showay, button_timecontrol,button_smartparset,
			button_allpwset,button_fastidset,button_tagfoucs,button_tagfound,button_report_sec,
			button_gpodemo,button_hopantime;
	ListView elist,listv_mfil;
	MyApplication myapp;
	public static int nowpower;

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

	private void showlist(String[] val,int type) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(val));

		if(type==0) {
			MyAdapter_fre mAdapter = new MyAdapter_fre(list, this);
			// 绑定Adapter

			elist.setAdapter(mAdapter);
		}
		else if(type==1){

			MyAdapter_mfil mAdapter = new MyAdapter_mfil(list, myapp.mfiltags,this);
			// 绑定Adapter
			listv_mfil.setAdapter(mAdapter);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab4_tablelayout);

		Application app = getApplication();
		myapp = (MyApplication) app;
		// 获取TabHost对象
		// 得到TabActivity中的TabHost对象
		tabHost_set = (TabHost) findViewById(R.id.tabhost4);
		// 如果没有继承TabActivity时，通过该种方法加载启动tabHost
		tabHost_set.setup();
		tabHost_set.getTabWidget().setOrientation(LinearLayout.VERTICAL);
        //.setIndicator(MyApplication.Constr_CONNECT,
		//							getResources().getDrawable(R.drawable.ic_launcher))
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab1")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4invenpra))
				.setContent(R.id.tab4_sub1_invusl));
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab2")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4antpow))
				.setContent(R.id.tab4_sub2_antpow));
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab3")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4regionfre))
				.setContent(R.id.tab4_sub3_invfre));
	/*	tabHost_set.addTab(tabHost_set
				.newTabSpec("tab4")
				.setIndicator(
				createIndicatorView(this, tabHost_set,
						MyApplication.Constr_sub4gen2opt))
				.setContent(R.id.tab4_sub4_gen2));
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab5")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4invenfil))
				.setContent(R.id.tab4_sub5_invfil));
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab6")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4addidata))
				.setContent(R.id.tab4_sub6_emd));
		tabHost_set.addTab(tabHost_set.newTabSpec("tab7")
				.setIndicator(createIndicatorView(this, tabHost_set, "GPIO"))
				.setContent(R.id.tab4_sub7_gpio));
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab8")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4others))
				.setContent(R.id.tab4_sub8_others));*/
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab4")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4addidata))
				.setContent(R.id.tab4_sub6_emd));
		tabHost_set.addTab(tabHost_set
				.newTabSpec("tab5")
				.setIndicator(
						createIndicatorView(this, tabHost_set,
								MyApplication.Constr_sub4quickly))
				.setContent(R.id.tab4_sub9_quickly));
		TabWidget tw = tabHost_set.getTabWidget();
		tw.getChildAt(0).setBackgroundColor(Color.BLUE);
		// tabHost2.setCurrentTab(2);
		spiq[0] = MyApplication.Auto;
		spigpodemo=MyApplication.gpodemo;

		spinner_ant1rpow = (Spinner) findViewById(R.id.spinner_ant1rpow);
		spinner_ant1wpow = (Spinner) findViewById(R.id.spinner_ant1wpow);
		spinner_ant2rpow = (Spinner) findViewById(R.id.spinner_ant2rpow);
		spinner_ant2wpow = (Spinner) findViewById(R.id.spinner_ant2wpow);
		spinner_ant3rpow = (Spinner) findViewById(R.id.spinner_ant3rpow);
		spinner_ant3wpow = (Spinner) findViewById(R.id.spinner_ant3wpow);
		spinner_ant4rpow = (Spinner) findViewById(R.id.spinner_ant4rpow);
		spinner_ant4wpow = (Spinner) findViewById(R.id.spinner_ant4wpow);
		spinner_ant5rpow = (Spinner) findViewById(R.id.spinner_ant5rpow);
		spinner_ant5wpow = (Spinner) findViewById(R.id.spinner_ant5wpow);
		spinner_ant6rpow = (Spinner) findViewById(R.id.spinner_ant6rpow);
		spinner_ant6wpow = (Spinner) findViewById(R.id.spinner_ant6wpow);
		spinner_ant7rpow = (Spinner) findViewById(R.id.spinner_ant7rpow);
		spinner_ant7wpow = (Spinner) findViewById(R.id.spinner_ant7wpow);
		spinner_ant8rpow = (Spinner) findViewById(R.id.spinner_ant8rpow);
		spinner_ant8wpow = (Spinner) findViewById(R.id.spinner_ant8wpow);
		spinner_ant9rpow = (Spinner) findViewById(R.id.spinner_ant9rpow);
		spinner_ant9wpow = (Spinner) findViewById(R.id.spinner_ant9wpow);
		spinner_ant10rpow = (Spinner) findViewById(R.id.spinner_ant10rpow);
		spinner_ant10wpow = (Spinner) findViewById(R.id.spinner_ant10wpow);
		spinner_ant11rpow = (Spinner) findViewById(R.id.spinner_ant11rpow);
		spinner_ant11wpow = (Spinner) findViewById(R.id.spinner_ant11wpow);
		spinner_ant12rpow = (Spinner) findViewById(R.id.spinner_ant12rpow);
		spinner_ant12wpow = (Spinner) findViewById(R.id.spinner_ant12wpow);
		spinner_ant13rpow = (Spinner) findViewById(R.id.spinner_ant13rpow);
		spinner_ant13wpow = (Spinner) findViewById(R.id.spinner_ant13wpow);
		spinner_ant14rpow = (Spinner) findViewById(R.id.spinner_ant14rpow);
		spinner_ant14wpow = (Spinner) findViewById(R.id.spinner_ant14wpow);
		spinner_ant15rpow = (Spinner) findViewById(R.id.spinner_ant15rpow);
		spinner_ant15wpow = (Spinner) findViewById(R.id.spinner_ant15wpow);
		spinner_ant16rpow = (Spinner) findViewById(R.id.spinner_ant16rpow);
		spinner_ant16wpow = (Spinner) findViewById(R.id.spinner_ant16wpow);
		// /*
		arradp_pow = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spipow);
		arradp_pow
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_ant1rpow.setAdapter(arradp_pow);
		spinner_ant1wpow.setAdapter(arradp_pow);
		spinner_ant2rpow.setAdapter(arradp_pow);
		spinner_ant2wpow.setAdapter(arradp_pow);
		spinner_ant3rpow.setAdapter(arradp_pow);
		spinner_ant3wpow.setAdapter(arradp_pow);
		spinner_ant4rpow.setAdapter(arradp_pow);
		spinner_ant4wpow.setAdapter(arradp_pow);
		spinner_ant5rpow.setAdapter(arradp_pow);
		spinner_ant5wpow.setAdapter(arradp_pow);
		spinner_ant6rpow.setAdapter(arradp_pow);
		spinner_ant6wpow.setAdapter(arradp_pow);
		spinner_ant7rpow.setAdapter(arradp_pow);
		spinner_ant7wpow.setAdapter(arradp_pow);
		spinner_ant8rpow.setAdapter(arradp_pow);
		spinner_ant8wpow.setAdapter(arradp_pow);
		spinner_ant9rpow.setAdapter(arradp_pow);
		spinner_ant9wpow.setAdapter(arradp_pow);
		spinner_ant10rpow.setAdapter(arradp_pow);
		spinner_ant10wpow.setAdapter(arradp_pow);
		spinner_ant11rpow.setAdapter(arradp_pow);
		spinner_ant11wpow.setAdapter(arradp_pow);
		spinner_ant12rpow.setAdapter(arradp_pow);
		spinner_ant12wpow.setAdapter(arradp_pow);
		spinner_ant13rpow.setAdapter(arradp_pow);
		spinner_ant13wpow.setAdapter(arradp_pow);
		spinner_ant14rpow.setAdapter(arradp_pow);
		spinner_ant14wpow.setAdapter(arradp_pow);
		spinner_ant15rpow.setAdapter(arradp_pow);
		spinner_ant15wpow.setAdapter(arradp_pow);
		spinner_ant16rpow.setAdapter(arradp_pow);
		spinner_ant16wpow.setAdapter(arradp_pow);

		spinner_ant2rpow.setEnabled(false);
		spinner_ant2wpow.setEnabled(false);
		spinner_ant3rpow.setEnabled(false);
		spinner_ant3wpow.setEnabled(false);
		spinner_ant4rpow.setEnabled(false);
		spinner_ant4wpow.setEnabled(false);
		spinner_ant5rpow.setEnabled(false);
		spinner_ant5wpow.setEnabled(false);
		spinner_ant6rpow.setEnabled(false);
		spinner_ant6wpow.setEnabled(false);
		spinner_ant7rpow.setEnabled(false);
		spinner_ant7wpow.setEnabled(false);
		spinner_ant8rpow.setEnabled(false);
		spinner_ant8wpow.setEnabled(false);
		spinner_ant9rpow.setEnabled(false);
		spinner_ant9wpow.setEnabled(false);
		spinner_ant10rpow.setEnabled(false);
		spinner_ant10wpow.setEnabled(false);
		spinner_ant11rpow.setEnabled(false);
		spinner_ant11wpow.setEnabled(false);
		spinner_ant12rpow.setEnabled(false);
		spinner_ant12wpow.setEnabled(false);
		spinner_ant13rpow.setEnabled(false);
		spinner_ant13wpow.setEnabled(false);
		spinner_ant14rpow.setEnabled(false);
		spinner_ant14wpow.setEnabled(false);
		spinner_ant15rpow.setEnabled(false);
		spinner_ant15wpow.setEnabled(false);
		spinner_ant16rpow.setEnabled(false);
		spinner_ant16wpow.setEnabled(false);
		// */

		spinner_sesion = (Spinner) findViewById(R.id.spinner_gen2session);
		arrdp_ses = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spises);
		arrdp_ses
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_sesion.setAdapter(arrdp_ses);

		spinner_q = (Spinner) findViewById(R.id.spinner_gen2q);
		arrdp_q = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spiq);
		arrdp_q.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_q.setAdapter(arrdp_q);

		spinner_wmode = (Spinner) findViewById(R.id.spinner_gen2wmode);
		arrdp_wmod = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spiwmod);
		arrdp_wmod
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_wmode.setAdapter(arrdp_wmod);

		spinner_blf = (Spinner) findViewById(R.id.spinner_gen2blf);
		arrdp_blf = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spiblf);
		arrdp_blf
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_blf.setAdapter(arrdp_blf);

		spinner_maxlen = (Spinner) findViewById(R.id.spinner_gen2maxl);
		arrdp_mlen = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spimlen);
		arrdp_mlen
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_maxlen.setAdapter(arrdp_mlen);

		spinner_target = (Spinner) findViewById(R.id.spinner_target);
		arrdp_tget = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spitget);
		arrdp_tget
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_target.setAdapter(arrdp_tget);

		spinner_g2code = (Spinner) findViewById(R.id.spinner_gen2code);
		arrdp_g2cod = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spigcod);
		arrdp_g2cod
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_g2code.setAdapter(arrdp_g2cod);

		spinner_tari = (Spinner) findViewById(R.id.spinner_gen2tari);
		arrdp_tari = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spitari);
		arrdp_tari
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_tari.setAdapter(arrdp_tari);

		spinner_emdbank = (Spinner) findViewById(R.id.spinner_emdbank);
		arrdp_bank = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spibank);
		arrdp_bank
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_emdbank.setAdapter(arrdp_bank);

		spinner_filbank = (Spinner) findViewById(R.id.spinner_invfbank);
		arrdp_fbank = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spifbank);
		arrdp_fbank
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_filbank.setAdapter(arrdp_fbank);

		spinner_invmode = (Spinner) findViewById(R.id.spinner_invmode);
		arrdp_invmo = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spinvmo);
		arrdp_invmo
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_invmode.setAdapter(arrdp_invmo);

		spinner_6btzsd = (Spinner) findViewById(R.id.spinner_6bdlt);
		arrdp_6btzsd = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spi6btzsd);
		arrdp_6btzsd
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_6btzsd.setAdapter(arrdp_6btzsd);

		spinner_delmi = (Spinner) findViewById(R.id.spinner_6bdp);
		arrdp_delm = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spidelm);
		arrdp_delm
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_delmi.setAdapter(arrdp_delm);

		spinner_region = (Spinner) findViewById(R.id.spinner_region);
		arradp_reg = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, MyApplication.spireg);
		arradp_reg
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_region.setAdapter(arradp_reg);

		spinner_persen = (Spinner) findViewById(R.id.spinner_per);
		arrdp_per = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spiperst);
		arrdp_per
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_persen.setAdapter(arrdp_per);
		
		spinner_qmode = (Spinner) findViewById(R.id.spinner_qmode);
		arrdp_qmode = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, myapp.spiqmode);
		arrdp_qmode
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_qmode.setAdapter(arrdp_qmode);
		spinner_qmode.setSelection(myapp.qmode);
		
		spinner_gpodemo = (Spinner) findViewById(R.id.spinner_gpodemo);
		arrdp_gpodemo = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spigpodemo);
		arrdp_gpodemo
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_gpodemo.setAdapter(arrdp_gpodemo);
	 

		rg_antcheckenable = (RadioGroup) findViewById(R.id.radioGroup_antcheck);
		button_getantpower = (Button) findViewById(R.id.button_antpowget);
		button_setantpower = (Button) findViewById(R.id.button_antpowset);
		button_getantcheck = (Button) findViewById(R.id.button_checkantget);
		button_setantcheck = (Button) findViewById(R.id.button_checkantset);

		button_getgen2ses = (Button) findViewById(R.id.button_gen2sesget);
		button_setgen2ses = (Button) findViewById(R.id.button_gen2sesset);
		button_getgen2q = (Button) findViewById(R.id.button_gen2qget);
		button_setgen2q = (Button) findViewById(R.id.button_gen2qset);
		button_getwmode = (Button) findViewById(R.id.button_gen2wmodeget);
		button_setwmode = (Button) findViewById(R.id.button_gen2wmodeset);
		button_getgen2blf = (Button) findViewById(R.id.button_gen2blfget);
		button_setgenblf = (Button) findViewById(R.id.button_gen2blfset);
		button_getgen2maxl = (Button) findViewById(R.id.button_gen2mlget);
		button_setgen2maxl = (Button) findViewById(R.id.button_gen2mlset);
		button_getgen2targ = (Button) findViewById(R.id.button_target);
		button_setgen2targ = (Button) findViewById(R.id.button_targetset);
		button_getgen2code = (Button) findViewById(R.id.button_codeget);
		button_setgen2code = (Button) findViewById(R.id.button_codeset);
		button_getgen2tari = (Button) findViewById(R.id.button_gen2tariget);
		button_setgen2tari = (Button) findViewById(R.id.button_gen2tariset);

		button_setgpo = (Button) findViewById(R.id.button_gposet);
		button_getgpi = (Button) findViewById(R.id.button_gpiget);

		button_gettempture = (Button) findViewById(R.id.button_tempure);
		button_quicklyread = (Button) findViewById(R.id.button_nostop);
		button_smartparset = (Button) findViewById(R.id.button_smartparset);
		button_nxpu8 = (Button) findViewById(R.id.button_nxpu8set);
		button_showay = (Button) findViewById(R.id.button_showopset);
		button_allpwset = (Button) findViewById(R.id.button_setallsamepw);
		button_timecontrol=(Button) findViewById(R.id.button_timecontrol);
	
		button_report_sec = (Button) findViewById(R.id.button_report_sec);
		button_fastidset=(Button) findViewById(R.id.button_fastodset);
		button_tagfoucs=(Button) findViewById(R.id.button_tagfoucs);
		button_tagfound=(Button) findViewById(R.id.button_tagfound);
		button_gpodemo = (Button) findViewById(R.id.button_gpodemoset);
		button_hopantime= (Button) findViewById(R.id.button_antimeset);
		cb_gpo1 = (CheckBox) findViewById(R.id.checkBox_gpo1);
		cb_gpo2 = (CheckBox) findViewById(R.id.checkBox_gpo2);
		cb_gpo3 = (CheckBox) findViewById(R.id.checkBox_gpo3);
		cb_gpo4 = (CheckBox) findViewById(R.id.checkBox_gpo4);
		cb_gpi1 = (CheckBox) findViewById(R.id.checkBox_gpi1);
		cb_gpi2 = (CheckBox) findViewById(R.id.checkBox_gpi2);
		cb_gpi3 = (CheckBox) findViewById(R.id.checkBox_gpi3);
		cb_gpi4 = (CheckBox) findViewById(R.id.checkBox_gpi4);

		cb_gen2 = (CheckBox) findViewById(R.id.checkBox_invgen2);
		cb_6b = (CheckBox) findViewById(R.id.checkBox_inv6b);
		cb_ipx64 = (CheckBox) findViewById(R.id.checkBox_invipx64);
		cb_ipx256 = (CheckBox) findViewById(R.id.checkBox_invipx256);

		cb_ant1 = (CheckBox) findViewById(R.id.checkBox_ant1);
		cb_ant2 = (CheckBox) findViewById(R.id.checkBox_ant2);
		cb_ant3 = (CheckBox) findViewById(R.id.checkBox_ant3);
		cb_ant4 = (CheckBox) findViewById(R.id.checkBox_ant4);
		cb_ant5 = (CheckBox) findViewById(R.id.checkBox_ant5);
		cb_ant6 = (CheckBox) findViewById(R.id.checkBox_ant6);
		cb_ant7 = (CheckBox) findViewById(R.id.checkBox_ant7);
		cb_ant8 = (CheckBox) findViewById(R.id.checkBox_ant8);
		cb_ant9 = (CheckBox) findViewById(R.id.checkBox_ant9);
		cb_ant10 = (CheckBox) findViewById(R.id.checkBox_ant10);
		cb_ant11 = (CheckBox) findViewById(R.id.checkBox_ant11);
		cb_ant12 = (CheckBox) findViewById(R.id.checkBox_ant12);
		cb_ant13 = (CheckBox) findViewById(R.id.checkBox_ant13);
		cb_ant14 = (CheckBox) findViewById(R.id.checkBox_ant14);
		cb_ant15 = (CheckBox) findViewById(R.id.checkBox_ant15);
		cb_ant16 = (CheckBox) findViewById(R.id.checkBox_ant16);
		cb_allsel = (CheckBox) findViewById(R.id.checkBox_allselect);
		cb_report_rec=(CheckBox) findViewById(R.id.checkBox_report_sec);
		cb_report_pos= (CheckBox) findViewById(R.id.checkBox_report_pos);
		cb_report_tep= (CheckBox) findViewById(R.id.checkBox_report_tep);
		cb_fastid= (CheckBox) findViewById(R.id.checkBox_fastid);
		cb_tagfoucs=(CheckBox) findViewById(R.id.checkBox_tagfoucs);
		cb_tagfound=(CheckBox) findViewById(R.id.checkBox_tagfound);
		
		if (myapp.Rparams.uants != null) {
			cb_ant1.setChecked(false);
			cb_ant2.setChecked(false);
			cb_ant3.setChecked(false);
			cb_ant4.setChecked(false);
			cb_ant5.setChecked(false);
			cb_ant6.setChecked(false);
			cb_ant7.setChecked(false);
			cb_ant8.setChecked(false);
			cb_ant9.setChecked(false);
			cb_ant10.setChecked(false);
			cb_ant11.setChecked(false);
			cb_ant12.setChecked(false);
			cb_ant13.setChecked(false);
			cb_ant14.setChecked(false);
			cb_ant15.setChecked(false);
			cb_ant16.setChecked(false);
			for (int i = 0; i < myapp.Rparams.uants.length; i++) {
				switch (myapp.Rparams.uants[i]) {
				case 1:
					cb_ant1.setChecked(true);
					break;
				case 2:
					cb_ant2.setChecked(true);
					break;
				case 3:
					cb_ant3.setChecked(true);
					break;
				case 4:
					cb_ant4.setChecked(true);
					break;
				case 5:
					cb_ant5.setChecked(true);
					break;
				case 6:
					cb_ant6.setChecked(true);
					break;
				case 7:
					cb_ant7.setChecked(true);
					break;
				case 8:
					cb_ant8.setChecked(true);
					break;
				case 9:
					cb_ant9.setChecked(true);
					break;
				case 10:
					cb_ant10.setChecked(true);
					break;
				case 11:
					cb_ant11.setChecked(true);
					break;
				case 12:
					cb_ant12.setChecked(true);
					break;
				case 13:
					cb_ant13.setChecked(true);
					break;
				case 14:
					cb_ant14.setChecked(true);
					break;
				case 15:
					cb_ant15.setChecked(true);
					break;
				case 16:
					cb_ant16.setChecked(true);
					break;
				}
			}
		}

		cbmf_readcount = (CheckBox) this.findViewById(R.id.checkBox_readcount);
		cbmf_rssi = (CheckBox) this.findViewById(R.id.checkBox_rssi);
		cbmf_ant = (CheckBox) this.findViewById(R.id.checkBox_ant);
		cbmf_fre = (CheckBox) this.findViewById(R.id.checkBox_frequency);
		cbmf_time = (CheckBox) this.findViewById(R.id.checkBox_time);
		cbmf_rfu = (CheckBox) this.findViewById(R.id.checkBox_rfu);
		cbmf_pro = (CheckBox) this.findViewById(R.id.checkBox_pro);
		cbmf_dl = (CheckBox) this.findViewById(R.id.checkBox_tagdatalen);
		cb_showuinemd = (CheckBox) findViewById(R.id.checkBox_showemd);
		cb_showuinant = (CheckBox) findViewById(R.id.checkBox_showant);
		cb_epcup= (CheckBox) findViewById(R.id.checkBox_epcup);
		// cb_nostop.setChecked(true);

		button_getemd = (Button) findViewById(R.id.button_getemd);
		button_setemd = (Button) findViewById(R.id.button_setemd);
		rg_emdenable = (RadioGroup) findViewById(R.id.radioGroup_emdenable);
		button_getfil = (Button) findViewById(R.id.button_getfil);
		button_setfil = (Button) findViewById(R.id.button_setfil);
		button_getmfil= (Button) findViewById(R.id.button_getmfil);
		button_setmfil= (Button) findViewById(R.id.button_setmfil);
		button_getreg = (Button) findViewById(R.id.button_getregion);
		button_setreg = (Button) findViewById(R.id.button_setregion);
		button_getfre = (Button) findViewById(R.id.button_getfre);
		button_setfre = (Button) findViewById(R.id.button_setfre);
		button_getusl = (Button) findViewById(R.id.button_invuslget);
		button_setusl = (Button) findViewById(R.id.button_invuslset);

		button_invproset = (Button) findViewById(R.id.button_invproset);
		button_opproget = (Button) findViewById(R.id.button_opproget);
		button_opproset = (Button) findViewById(R.id.button_opproset);

		button_invantsset = (Button) findViewById(R.id.button_invantsset);
		button_invantsget = (Button) findViewById(R.id.button_invantsget);

		rg_invfilenable = (RadioGroup) findViewById(R.id.radioGroup_enablefil);
		rg_invfilmatch = (RadioGroup) findViewById(R.id.radioGroup_invmatch);
		rg_nxpu8 = (RadioGroup) findViewById(R.id.radioGroup_nxpu8);
		elist = (ListView) this.findViewById(R.id.listView_frequency);
		listv_mfil= (ListView) this.findViewById(R.id.listView_mfil);

		button_oantuqget = (Button) findViewById(R.id.button_oantuqget);
		button_oantuqset = (Button) findViewById(R.id.button_oantuqset);
		button_odatauqget = (Button) findViewById(R.id.button_odatauqget);
		button_odatauqset = (Button) findViewById(R.id.button_odatauqset);
		button_hrssiget = (Button) findViewById(R.id.button_hrssiget);
		button_hrssiset = (Button) findViewById(R.id.button_hrssiset);
		button_invmodeget = (Button) findViewById(R.id.button_invmodeget);
		button_invmodeset = (Button) findViewById(R.id.button_invmodeset);
		button_6bdpget = (Button) findViewById(R.id.button_6bdpget);
		button_6bdpset = (Button) findViewById(R.id.button_6bdpset);
		button_6bdltget = (Button) findViewById(R.id.button_6bdltget);
		button_6bdltset = (Button) findViewById(R.id.button_6bdltset);
		button_6bblfget = (Button) findViewById(R.id.button_6bblfget);
		button_6bblfset = (Button) findViewById(R.id.button_6bblfset);
		cb_oant = (CheckBox) findViewById(R.id.checkBox_oantuq);
		cb_odata = (CheckBox) findViewById(R.id.checkBox_odatauq);
		cb_hrssi = (CheckBox) findViewById(R.id.checkBox_hrssi);
		if (myapp.antportc >= 2) {
			spinner_ant2rpow.setEnabled(true);
			spinner_ant2wpow.setEnabled(true);
		}
		if (myapp.antportc >= 3) {
			spinner_ant3rpow.setEnabled(true);
			spinner_ant3wpow.setEnabled(true);
		}
		if (myapp.antportc >= 4) {
			spinner_ant4rpow.setEnabled(true);
			spinner_ant4wpow.setEnabled(true);
		}
		if(myapp.antportc >=8)	
		{
			spinner_ant5rpow.setEnabled(true);
			spinner_ant5wpow.setEnabled(true);

			spinner_ant6rpow.setEnabled(true);
			spinner_ant6wpow.setEnabled(true);

			spinner_ant7rpow.setEnabled(true);
			spinner_ant7wpow.setEnabled(true);

			spinner_ant8rpow.setEnabled(true);
			spinner_ant8wpow.setEnabled(true);
		}
		if (myapp.antportc >= 16) {
			

			spinner_ant9rpow.setEnabled(true);
			spinner_ant9wpow.setEnabled(true);

			spinner_ant10rpow.setEnabled(true);
			spinner_ant10wpow.setEnabled(true);

			spinner_ant11rpow.setEnabled(true);
			spinner_ant11wpow.setEnabled(true);

			spinner_ant12rpow.setEnabled(true);
			spinner_ant12wpow.setEnabled(true);

			spinner_ant13rpow.setEnabled(true);
			spinner_ant13wpow.setEnabled(true);

			spinner_ant14rpow.setEnabled(true);
			spinner_ant14wpow.setEnabled(true);

			spinner_ant15rpow.setEnabled(true);
			spinner_ant15wpow.setEnabled(true);

			spinner_ant16rpow.setEnabled(true);
			spinner_ant16wpow.setEnabled(true);
		}

		// 绑定listView的监听器
		elist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
				ViewHolder_fre holder = (ViewHolder_fre) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();
				// 将CheckBox的选中状况记录下来
				MyAdapter_fre.getIsSelected().put(arg2, holder.cb.isChecked());
			}

		});

		listv_mfil.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
				ViewHolder_mfil holder = (ViewHolder_mfil) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();
				// 将CheckBox的选中状况记录下来
				MyAdapter_mfil.getIsSelected().put(arg2, holder.cb.isChecked());
			}

		});

		/**
		 * 获取是否天线检测
		 */
		button_getantcheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val2 = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, val2);

					if (er == READER_ERR.MT_OK_ERR) {
						if (val2[0] == 0)
							rg_antcheckenable.check(rg_antcheckenable
									.getChildAt(0).getId());
						else
							rg_antcheckenable.check(rg_antcheckenable
									.getChildAt(1).getId());
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_excep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 配置是否天线检测
		 */
		button_setantcheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					READER_ERR er;
					if (commfun.SortGroup(rg_antcheckenable) == 0)
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
								new int[] { 0 });
					else
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
								new int[] { 1 });
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.checkant = commfun
								.SortGroup(rg_antcheckenable);
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});
		/**
		 * 获取天线功率
		 */
		button_getantpower.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					AntPowerConf apcf2 = myapp.Mreader.new AntPowerConf();

					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf2);

					if (er == READER_ERR.MT_OK_ERR) {
						for (int i = 0; i < apcf2.antcnt; i++) {
							if (i == 0) {
								spinner_ant1rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant1wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 1) {
								spinner_ant2rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant2wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 2) {
								spinner_ant3rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant3wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 3) {
								spinner_ant4rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant4wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 4) {
								spinner_ant5rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant5wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 5) {
								spinner_ant6rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant6wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 6) {
								spinner_ant7rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant7wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 7) {
								spinner_ant8rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant8wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 8) {
								spinner_ant9rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant9wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 9) {
								spinner_ant10rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant10wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 10) {
								spinner_ant11rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant11wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 11) {
								spinner_ant12rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant12wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 12) {
								spinner_ant13rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant13wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 13) {
								spinner_ant14rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant14wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 14) {
								spinner_ant15rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant15wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							} else if (i == 15) {
								spinner_ant16rpow
										.setSelection((apcf2.Powers[i].readPower - minpw) / 100);
								spinner_ant16wpow
										.setSelection((apcf2.Powers[i].writePower - minpw) / 100);
							}

						}

					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_getcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置天线功率
		 */
		button_setantpower.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] rp = new int[16];
				int[] wp = new int[16];

				rp[0] = spinner_ant1rpow.getSelectedItemPosition();
				rp[1] = spinner_ant2rpow.getSelectedItemPosition();
				rp[2] = spinner_ant3rpow.getSelectedItemPosition();
				rp[3] = spinner_ant4rpow.getSelectedItemPosition();

				rp[4] = spinner_ant5rpow.getSelectedItemPosition();
				rp[5] = spinner_ant6rpow.getSelectedItemPosition();
				rp[6] = spinner_ant7rpow.getSelectedItemPosition();
				rp[7] = spinner_ant8rpow.getSelectedItemPosition();

				rp[8] = spinner_ant9rpow.getSelectedItemPosition();
				rp[9] = spinner_ant10rpow.getSelectedItemPosition();
				rp[10] = spinner_ant11rpow.getSelectedItemPosition();
				rp[11] = spinner_ant12rpow.getSelectedItemPosition();

				rp[12] = spinner_ant13rpow.getSelectedItemPosition();
				rp[13] = spinner_ant14rpow.getSelectedItemPosition();
				rp[14] = spinner_ant15rpow.getSelectedItemPosition();
				rp[15] = spinner_ant16rpow.getSelectedItemPosition();

				wp[0] = spinner_ant1wpow.getSelectedItemPosition();
				wp[1] = spinner_ant2wpow.getSelectedItemPosition();
				wp[2] = spinner_ant3wpow.getSelectedItemPosition();
				wp[3] = spinner_ant4wpow.getSelectedItemPosition();

				wp[4] = spinner_ant5wpow.getSelectedItemPosition();
				wp[5] = spinner_ant6wpow.getSelectedItemPosition();
				wp[6] = spinner_ant7wpow.getSelectedItemPosition();
				wp[7] = spinner_ant8wpow.getSelectedItemPosition();

				wp[8] = spinner_ant9wpow.getSelectedItemPosition();
				wp[9] = spinner_ant10wpow.getSelectedItemPosition();
				wp[10] = spinner_ant11wpow.getSelectedItemPosition();
				wp[11] = spinner_ant12wpow.getSelectedItemPosition();

				wp[12] = spinner_ant13wpow.getSelectedItemPosition();
				wp[13] = spinner_ant14wpow.getSelectedItemPosition();
				wp[14] = spinner_ant15wpow.getSelectedItemPosition();
				wp[15] = spinner_ant16wpow.getSelectedItemPosition();

				AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
				apcf.antcnt = myapp.antportc;
				int[] rpow = new int[apcf.antcnt];
				int[] wpow = new int[apcf.antcnt];
				for (int i = 0; i < apcf.antcnt; i++) {
					AntPower jaap = myapp.Mreader.new AntPower();
					jaap.antid = i + 1;
					jaap.readPower = (short) (minpw + 100 * rp[i]);// minpw-3000
					rpow[i] = jaap.readPower;

					jaap.writePower = (short) (minpw + 100 * wp[i]);
					wpow[i] = jaap.writePower;
					apcf.Powers[i] = jaap;
				}

				try {
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
					Sub4TabActivity.nowpower=apcf.Powers[0].readPower;
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.rpow = rpow;
						myapp.Rparams.wpow = wpow;
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});
		/**
		 * 获取gen2 session
		 */
		button_getgen2ses.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val2 = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val2);

					if (er == READER_ERR.MT_OK_ERR) {
						spinner_sesion.setSelection(val2[0]);
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 获取gen2 session
		 */
		button_setgen2ses.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };
					val[0] = spinner_sesion.getSelectedItemPosition();

					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.session = val[0];
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});

		/**
		 * 获取gen2 q值
		 */
		button_getgen2q.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val);
					if (val[0] == 255)
						val[0] = -1;
					if (er == READER_ERR.MT_OK_ERR) {
						spinner_q.setSelection(val[0] + 1);
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置gen2 q值
		 */
		button_setgen2q.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };
					val[0] = spinner_q.getSelectedItemPosition() - 1;
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.qv = val[0];
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});

		/**
		 * 获取写模式
		 */
		button_getwmode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE, val);

					if (er == READER_ERR.MT_OK_ERR) {
						if (val[0] == 0)
							spinner_wmode.setSelection(0);
						else if (val[0] == 1)
							spinner_wmode.setSelection(1);
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置写模式
		 */
		button_setwmode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { spinner_wmode
							.getSelectedItemPosition() };

					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE, val);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.wmode = val[0];
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});

		/**
		 * 获取gen2 blf
		 */
		button_getgen2blf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_BLF, val);

					if (er == READER_ERR.MT_OK_ERR) {
						switch (val[0]) {
						case 40:
							spinner_blf.setSelection(0);
							break;
						case 250:
							spinner_blf.setSelection(1);
							break;
						case 400:
							spinner_blf.setSelection(2);
							break;
						case 640:
							spinner_blf.setSelection(3);
							break;
						}
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置gen2 blf
		 */
		button_setgenblf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };

					switch (spinner_blf.getSelectedItemPosition()) {
					case 0:
						val[0] = 40;
						break;
					case 1:
						val[0] = 250;
						break;
					case 2:
						val[0] = 400;
						break;
					case 3:
						val[0] = 640;
						break;
					}
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_BLF, val);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.blf = val[0];
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

		});

		/**
		 * 获取gen2 最大epc 长度
		 */
		button_getgen2maxl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { -1 };
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN, val);
				if (er == READER_ERR.MT_OK_ERR) {
					if (val[0] == 96)
						spinner_maxlen.setSelection(0);
					else
						spinner_maxlen.setSelection(1);
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 设置gen2 最大epc 长度
		 */
		button_setgen2maxl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { spinner_maxlen
						.getSelectedItemPosition() == 0 ? 96 : 496 };
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN, val);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.maxlen = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 获取gen2 target
		 */
		button_getgen2targ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					int[] val = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, val);
					if (er == READER_ERR.MT_OK_ERR) {
						spinner_target.setSelection(val[0]);
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置gen2 target
		 */
		button_setgen2targ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { spinner_target
						.getSelectedItemPosition() };
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, val);

				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.gen2tari = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 获取gen2 tag编码
		 */
		button_getgen2code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { -1 };
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, val);
				if (er == READER_ERR.MT_OK_ERR) {
					if(val[0]<=3)
					spinner_g2code.setSelection(val[0]);
					else if(val[0]>100)
                    {
                    	if(val[0]==101)
                        spinner_g2code.setSelection(10);
						if(val[0]==103)
							spinner_g2code.setSelection(11);
						if(val[0]==105)
							spinner_g2code.setSelection(12);
						if(val[0]==107)
							spinner_g2code.setSelection(13);
						if(val[0]==111)
							spinner_g2code.setSelection(14);
						if(val[0]==112)
							spinner_g2code.setSelection(15);
						if(val[0]==113)
							spinner_g2code.setSelection(16);
						if(val[0]==115)
							spinner_g2code.setSelection(17);
                    }
					else
						spinner_g2code.setSelection(4+val[0]-0x10);	
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 设置gen2 tag编码
		 */
		button_setgen2code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { spinner_g2code
						.getSelectedItemPosition() };
				
				
				READER_ERR er =READER_ERR.MT_OK_ERR;
				
				if(val[0]<=3)
				er=myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, val);
				 else if(val[0]>3&&val[0]<=9)
				 {	
					 val[0]=0x10+val[0]-4;
					 er=myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING,
								val);}
				 else if(val[0]>9)
				{
					if(val[0]==10)
					val[0]=101;
					else if(val[0]==11)
					val[0]=103;
					else if(val[0]==12)
						val[0]=105;
					else if(val[0]==13)
						val[0]=107;
					else if(val[0]==14)
						val[0]=111;
					else if(val[0]==15)
						val[0]=112;
					else if(val[0]==16)
						val[0]=113;
					else if(val[0]==17)
						val[0]=115;
					er=myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING,
							val);
				}
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.gen2code = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}
		});

		/**
		 * 获取gen2 tari
		 */
		button_getgen2tari.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {

					int[] val = new int[] { -1 };
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_POTL_GEN2_TARI, val);
					if (er == READER_ERR.MT_OK_ERR) {
						spinner_tari.setSelection(val[0]);
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置gen2 tari
		 */
		button_setgen2tari.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { spinner_tari.getSelectedItemPosition() };
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_TARI, val);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.gen2tari = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 获取gpi
		 */
		button_getgpi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[][] gpi = new int[4][1];

				for (int i = 0; i < gpi.length; i++) {
					READER_ERR er = myapp.Mreader.GetGPI(i + 1, gpi[i]);

					if (er == READER_ERR.MT_OK_ERR) {
						if (i == 1) {
							if (gpi[i][0] == 1)
								cb_gpi1.setChecked(true);
							else
								cb_gpi1.setChecked(false);
						} else if (i == 2) {
							if (gpi[i][0] == 1)
								cb_gpi2.setChecked(true);
							else
								cb_gpi2.setChecked(false);
						} else if (i == 3) {
							if (gpi[i][0] == 1)
								cb_gpi3.setChecked(true);
							else
								cb_gpi3.setChecked(false);
						} else if (i == 4) {
							if (gpi[i][0] == 1)
								cb_gpi4.setChecked(true);
							else
								cb_gpi4.setChecked(false);
						}
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 设置gpo
		 */
		button_setgpo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[][] gpo = new int[4][1];
				for (int i = 0; i < gpo.length; i++) {
					if (i == 0)
						gpo[0][0] = cb_gpo1.isChecked() ? 1 : 0;
					else if (i == 1)
						gpo[1][0] = cb_gpo2.isChecked() ? 1 : 0;
					else if (i == 2)
						gpo[2][0] = cb_gpo3.isChecked() ? 1 : 0;
					else if (i == 3)
						gpo[3][0] = cb_gpo4.isChecked() ? 1 : 0;
				}
				for (int i = 0; i < gpo.length; i++) {
					READER_ERR er = myapp.Mreader.SetGPO(i + 1, gpo[i][0]);
					if (er == READER_ERR.MT_OK_ERR)
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 获取附加数据
		 */
		button_getemd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				EditText etst = (EditText) findViewById(R.id.editText_emdsadr);
				EditText etapwd = (EditText) findViewById(R.id.editText_emdacspwd);
				EditText etct = (EditText) findViewById(R.id.editText_emdcount);

				EmbededData_ST edst2 = myapp.Mreader.new EmbededData_ST();
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst2);

				if (er == READER_ERR.MT_OK_ERR) {
					if (edst2 == null || edst2.bytecnt == 0) {
						etst.setText("");
						etapwd.setText("");
						etct.setText("");
						rg_emdenable.check(rg_emdenable.getChildAt(0).getId());
						spinner_emdbank.setSelection(0);
					} else {
						// dlog.toDlog("android staddr:"+String.valueOf(edst2.startaddr));
						// dlog.toDlog("android bytecnt:"+String.valueOf(edst2.bytecnt));
						// dlog.toDlog("android bank:"+String.valueOf(edst2.bank));
						// *
						if (edst2.accesspwd != null) {
							char[] out = new char[edst2.accesspwd.length * 2];
							myapp.Mreader.Hex2Str(edst2.accesspwd,
									edst2.accesspwd.length, out);
							etapwd.setText(String.valueOf(out));
						}
						etst.setText(String.valueOf(edst2.startaddr));
						etct.setText(String.valueOf(edst2.bytecnt));
						rg_emdenable.check(rg_emdenable.getChildAt(1).getId());

						spinner_emdbank.setSelection(edst2.bank);
						// */
					}
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 设置附加数据
		 */
		button_setemd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (myapp.nxpu8 != 0) {
					Toast.makeText(Sub4TabActivity.this,
							"NXP U8 MODE is using emddata", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				try {
					EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();

					edst.accesspwd = null;

					if (commfun.SortGroup(rg_emdenable) == 1) {
						EditText etst = (EditText) findViewById(R.id.editText_emdsadr);
						EditText etapwd = (EditText) findViewById(R.id.editText_emdacspwd);
						EditText etct = (EditText) findViewById(R.id.editText_emdcount);
						edst.bank = spinner_emdbank.getSelectedItemPosition();
						edst.startaddr = Integer.valueOf(etst.getText()
								.toString());
						edst.bytecnt = Byte
								.parseByte(etct.getText().toString());
						if (!etapwd.getText().toString().equals("")) {
							edst.accesspwd = new byte[etapwd.getText().length() / 2];
							myapp.Mreader.Str2Hex(etapwd.getText().toString(),
									etapwd.getText().length(), edst.accesspwd);
						}
						//edst.accesspwd = null;
					} else
						edst.bytecnt = 0;

					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.emdadr = edst.startaddr;
						myapp.Rparams.emdbank = edst.bank;
						myapp.Rparams.emdbytec = edst.bytecnt;
						myapp.Rparams.emdenable = 1;
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception ex) {
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + ex.toString(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		/**
		 * 获取过滤条件
		 */
		button_getfil.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				TagFilter_ST tfst2 = myapp.Mreader.new TagFilter_ST();

				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_TAG_FILTER, tfst2);
				if (er == READER_ERR.MT_OK_ERR) {
					EditText et = (EditText) findViewById(R.id.editText_filterdata);
					EditText etadr = (EditText) findViewById(R.id.editText_invfilsadr);

					if (tfst2.flen == 0) {
						rg_invfilenable.check(rg_invfilenable.getChildAt(0)
								.getId());
						et.setText("");
						etadr.setText("");
						spinner_filbank.setSelection(0);
					} else {
						rg_invfilenable.check(rg_invfilenable.getChildAt(1)
								.getId());
						char[] fd = null;
						if (tfst2.flen < 4)
							fd = new char[1];
						else
							fd = new char[tfst2.flen %4==0?tfst2.flen /4:tfst2.flen /4+1];
						myapp.Mreader.Hex2Str(tfst2.fdata, 
								fd.length%2==0?fd.length/2:fd.length/2+1, fd);
						et.setText(String.valueOf(fd));
						etadr.setText(String.valueOf(tfst2.startaddr));
						spinner_filbank.setSelection(tfst2.bank - 1);

						if (tfst2.isInvert == 1)
							rg_invfilmatch.check(rg_invfilmatch.getChildAt(1)
									.getId());
						else
							rg_invfilmatch.check(rg_invfilmatch.getChildAt(0)
									.getId());
					}

				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}
		});

		/**
		 * 设置过滤条件
		 */
		button_setfil.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (myapp.nxpu8 != 0) {
					Toast.makeText(Sub4TabActivity.this,
							"NXP U8 MODE is using", Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();

					// flen 0 is that cancle filter as pass null.

					if (commfun.SortGroup(rg_invfilenable) == 1) {

						EditText et = (EditText) findViewById(R.id.editText_filterdata);
						int ln = et.getText().toString().length();
						if (ln == 1 || ln % 2 == 1)
							ln++;
						tfst.fdata = new byte[ln / 2];
						myapp.Mreader.Str2Hex(et.getText().toString(), et
								.getText().toString().length(), tfst.fdata);

						tfst.bank = spinner_filbank.getSelectedItemPosition() + 1;
						EditText etadr = (EditText) findViewById(R.id.editText_invfilsadr);
						tfst.flen = et.getText().toString().length() * 4;

						tfst.startaddr = Integer.valueOf(etadr.getText()
								.toString());

						int ma = commfun.SortGroup(rg_invfilmatch);
						if (ma == 1)
							tfst.isInvert = 1;
						else
							tfst.isInvert = 0;
					} else
						tfst = null;

					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
					if (er == READER_ERR.MT_OK_ERR) {
						if (tfst != null) {
							myapp.Rparams.filadr = tfst.startaddr;
							myapp.Rparams.filbank = tfst.bank;
							EditText et = (EditText) findViewById(R.id.editText_filterdata);
							myapp.Rparams.fildata = et.getText().toString();
							myapp.Rparams.filenable = 1;
							myapp.Rparams.filisinver = tfst.isInvert;
						} else {
							myapp.Rparams.filadr = 0;
							myapp.Rparams.filbank = 1;

							myapp.Rparams.fildata = "";
							myapp.Rparams.filenable = 0;
							myapp.Rparams.filisinver = 0;
						}

						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();

				} catch (Exception ex) {
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + ex.toString(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		button_getmfil.setOnClickListener(new OnClickListener() {

											 @Override
											 public void onClick(View arg0) {

												 Iterator<Map.Entry<String, Reader.TAGINFO>> iesb;
												 iesb = myapp.TagsMap.entrySet().iterator();
												 String[] epcs=new String[myapp.TagsMap.size()];
												 int p=0;
												 while (iesb.hasNext()) {
													 Reader.TAGINFO bd = iesb.next().getValue();
													 String linestr = Reader.bytes_Hexstr(bd.EpcId);
													 epcs[p++]=linestr;
												 }
												if(myapp.TagsMap.size()>0)
												 showlist(epcs,1);
											 }
										 });

		button_setmfil.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				myapp.mfiltags.clear();
				int c=listv_mfil.getCheckedItemCount();

				for (int i = 0; i < listv_mfil.getCount(); i++) {
					String temp = (String) listv_mfil.getItemAtPosition(i);

					if (listv_mfil.isItemChecked(i)) {
						myapp.mfiltags.add(temp);
					}
					if(MyAdapter_mfil.getIsSelected().get(i))
					{
						myapp.mfiltags.add(temp);
					}

				}

				READER_ERR err  = READER_ERR.MT_OK_ERR;

				if (myapp.mfiltags.size() > 0) {
					String[] mepc=new String[myapp.mfiltags.size()];
					for(int i=0;i<myapp.mfiltags.size();i++)
						mepc[i]=myapp.mfiltags.get(i);
					err = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_MULTISELECTORS, mepc);
					Toast.makeText(Sub4TabActivity.this,
							"Result:" + err.toString(),
							Toast.LENGTH_SHORT).show();
					EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();
//*
					edst.accesspwd = null;
					edst.bank = 0;
					edst.startaddr = 4;
					edst.bytecnt = 8;
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);

 //*/
				}
				else {
					err = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_MULTISELECTORS, null);
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, null);
				}
			}
		});

		/**
		 * 获取区域
		 */
		button_getreg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				spinner_region.setSelection(-1);
				Region_Conf[] rcf2 = new Region_Conf[1];
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
				if (er == READER_ERR.MT_OK_ERR) {
					switch (rcf2[0]) {
					case RG_PRC:
						spinner_region.setSelection(0);
						break;
					case RG_EU:
						spinner_region.setSelection(4);
						break;
					case RG_EU2:
						spinner_region.setSelection(5);
						break;
					case RG_EU3:
						spinner_region.setSelection(6);
						break;
					case RG_KR:
						spinner_region.setSelection(3);
						break;
					case RG_NA:
						spinner_region.setSelection(1);
						break;
					case RG_OPEN:
						spinner_region.setSelection(9);
						break;
					case RG_PRC2:
						spinner_region.setSelection(10);
						break;
					default:
						spinner_region.setSelection(0);
						break;
					}
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}
		});

		/**
		 * 设置区域
		 */
		button_setreg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Region_Conf rre;
				switch (spinner_region.getSelectedItemPosition()) {
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
				case 7:
				case 8:
					rre = Region_Conf.RG_NONE;
					break;
				case 9:
					rre = Region_Conf.RG_OPEN;
					break;
				case 10:
					rre = Region_Conf.RG_PRC2;
					break;
				default:
					rre = Region_Conf.RG_NONE;
					break;
				}
				if (rre == Region_Conf.RG_NONE&&
						(spinner_region.getSelectedItemPosition() != 7)) {
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_sub4unspreg,
							Toast.LENGTH_SHORT).show();
					return;
				}

				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rre);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.region = spinner_region
							.getSelectedItemPosition();
					myapp.Rparams.frelen = 0;
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();

					try {
						HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
						er = myapp.Mreader.ParamGet(
								Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);

						if (er == READER_ERR.MT_OK_ERR) {

							myapp.allhtb = commfun
									.Sort(hdst2.htb, hdst2.lenhtb);
						}
					} catch (Exception ex) {

					}
				} else {
					int[] vls = null;

					if (spinner_region.getSelectedItemPosition() == 7) {
						
						//India
						vls = new int[] { 865200, 865600, 866000, 866400,
								866800 };

					}

					if (vls != null) {
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_FREQUENCY_REGION,
								Region_Conf.RG_OPEN);
						if (er == READER_ERR.MT_OK_ERR) {

							HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
							hdst.lenhtb = vls.length;
							hdst.htb = vls;
							er = myapp.Mreader.ParamSet(
									Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE,
									hdst);
						}
					}
					else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		/**
		 * 获取频点表
		 */
		button_getfre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                //long st=System.currentTimeMillis();
				HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
				//int inv=(int) (System.currentTimeMillis()-st);
				//dlog.toDlog(String.valueOf(inv));
				int[] tablefre;
				if (er == READER_ERR.MT_OK_ERR) {

					tablefre = commfun.Sort(hdst2.htb, hdst2.lenhtb);
					String[] ssf = new String[hdst2.lenhtb];
					for (int i = 0; i < hdst2.lenhtb; i++) {
						ssf[i] = String.valueOf(tablefre[i]);
					}
					showlist(ssf,0);

				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 设置频点表
		 */
		button_setfre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ArrayList<Integer> lit = new ArrayList<Integer>();
				for (int i = 0; i < elist.getCount(); i++) {
					String temp = (String) elist.getItemAtPosition(i);
					if (MyAdapter_fre.getIsSelected().get(i)) {
						lit.add(Integer.valueOf(temp));
					}

				}
				if (lit.size() > 0) {
					int[] vls = commfun.CollectionTointArray(lit);
					HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
					hdst.lenhtb = vls.length;
					hdst.htb = vls;
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.frecys = hdst.htb;
						myapp.Rparams.frelen = hdst.lenhtb;
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				}

			}

		});

		/**
		 * 获取读时长以及间隔
		 */
		button_getusl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EditText ettime = (EditText) findViewById(R.id.editText_invtime);
				EditText etsleep = (EditText) findViewById(R.id.editText_invsleep);

				ettime.setText(String.valueOf((myapp.Rparams.readtime)));
				etsleep.setText(String.valueOf((myapp.Rparams.sleep)));
			}

		});

		/**
		 * 设置读时长以及间隔
		 */
		button_setusl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					EditText ettime = (EditText) findViewById(R.id.editText_invtime);
					EditText etsleep = (EditText) findViewById(R.id.editText_invsleep);
					myapp.Rparams.readtime = Integer.valueOf(ettime.getText()
							.toString());
					myapp.Rparams.sleep = Integer.valueOf(etsleep.getText()
							.toString());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_SetOk, Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 设置盘点协议
		 */
		button_invproset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				List<SL_TagProtocol> ltp = new ArrayList<SL_TagProtocol>();
				List<String> ls = new ArrayList<String>();
				if (cb_gen2.isChecked()) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);
					ls.add("GEN2");

				}
				if (cb_6b.isChecked()) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);
					// ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B_UCODE);
					ls.add("6B");
				}
				if (cb_ipx64.isChecked()) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);
					ls.add("IPX64");
				}
				if (cb_ipx256.isChecked()) {
					ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);
					ls.add("IPX256");
				}

				if (ltp.size() < 1) {
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_selpro, Toast.LENGTH_SHORT)
							.show();
					return;
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

				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.invpro.clear();
					myapp.Rparams.invpro.addAll(ls);
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});
		button_opproget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}

		});
		button_opproset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}

		});

		// 指定盘点天线
		button_invantsget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				cb_ant1.setChecked(false);
				cb_ant2.setChecked(false);
				cb_ant3.setChecked(false);
				cb_ant4.setChecked(false);
				cb_ant5.setChecked(false);
				cb_ant6.setChecked(false);
				cb_ant7.setChecked(false);
				cb_ant8.setChecked(false);
				cb_ant9.setChecked(false);
				cb_ant10.setChecked(false);
				cb_ant11.setChecked(false);
				cb_ant12.setChecked(false);
				cb_ant13.setChecked(false);
				cb_ant14.setChecked(false);
				cb_ant15.setChecked(false);
				cb_ant16.setChecked(false);

				ConnAnts_ST val = myapp.Mreader.new ConnAnts_ST();
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_READER_CONN_ANTS, val);
				if (er == READER_ERR.MT_OK_ERR) {

					for (int i = 0; i < val.antcnt; i++) {
						switch (val.connectedants[i]) {
						case 1:
							cb_ant1.setChecked(true);
							break;
						case 2:
							cb_ant2.setChecked(true);
							break;
						case 3:
							cb_ant3.setChecked(true);
							break;
						case 4:
							cb_ant4.setChecked(true);
							break;
						case 5:
							cb_ant5.setChecked(true);
							break;
						case 6:
							cb_ant6.setChecked(true);
							break;
						case 7:
							cb_ant7.setChecked(true);
							break;
						case 8:
							cb_ant8.setChecked(true);
							break;
						case 9:
							cb_ant9.setChecked(true);
							break;
						case 10:
							cb_ant10.setChecked(true);
							break;
						case 11:
							cb_ant11.setChecked(true);
							break;
						case 12:
							cb_ant12.setChecked(true);
							break;
						case 13:
							cb_ant13.setChecked(true);
							break;
						case 14:
							cb_ant14.setChecked(true);
							break;
						case 15:
							cb_ant15.setChecked(true);
							break;
						case 16:
							cb_ant16.setChecked(true);
							break;
						}
					}
				}
			}
		});

		// 指定盘点天线
		button_invantsset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				List<Integer> ltp = new ArrayList<Integer>();

				if (cb_ant1.isChecked())
					ltp.add(1);
				if (cb_ant2.isChecked())
					ltp.add(2);
				if (cb_ant3.isChecked())
					ltp.add(3);
				if (cb_ant4.isChecked())
					ltp.add(4);
				if (cb_ant5.isChecked())
					ltp.add(5);
				if (cb_ant6.isChecked())
					ltp.add(6);
				if (cb_ant7.isChecked())
					ltp.add(7);
				if (cb_ant8.isChecked())
					ltp.add(8);
				if (cb_ant9.isChecked())
					ltp.add(9);
				if (cb_ant10.isChecked())
					ltp.add(10);
				if (cb_ant11.isChecked())
					ltp.add(11);
				if (cb_ant12.isChecked())
					ltp.add(12);
				if (cb_ant13.isChecked())
					ltp.add(13);
				if (cb_ant14.isChecked())
					ltp.add(14);
				if (cb_ant15.isChecked())
					ltp.add(15);
				if (cb_ant16.isChecked())
					ltp.add(16);

				if (ltp.size() < 1) {
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_psiant, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				Integer[] ants = ltp.toArray(new Integer[ltp.size()]);
				myapp.Rparams.uants = new int[ants.length];
				for (int i = 0; i < ants.length; i++)
					myapp.Rparams.uants[i] = ants[i];

				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_SetOk, Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 获取是否天线唯一
		 */
		button_oantuqget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { -1 };
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYANT, val);

				if (er == READER_ERR.MT_OK_ERR) {
					cb_oant.setChecked(val[0] == 1 ? true : false);
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 设置是否天线唯一
		 */
		button_oantuqset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { -1 };
				val[0] = cb_oant.isChecked() ? 1 : 0;
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYANT, val);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.antq = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 获取是否附加数据唯一
		 */
		button_odatauqget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { -1 };
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA, val);

				if (er == READER_ERR.MT_OK_ERR) {
					cb_odata.setChecked(val[0] == 1 ? true : false);
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 设置是否附加数据唯一
		 */
		button_odatauqset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { -1 };
				val[0] = cb_odata.isChecked() ? 1 : 0;
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA, val);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.adataq = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 获取是否记录最高信号强度
		 */
		button_hrssiget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { -1 };
				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI, val);

				if (er == READER_ERR.MT_OK_ERR) {
					cb_hrssi.setChecked(val[0] == 1 ? true : false);
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 设置是否记录最高信号强度
		 */
		button_hrssiset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { -1 };
				val[0] = cb_hrssi.isChecked() ? 1 : 0;
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI, val);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.rhssi = val[0];
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 获取盘点搜索模式
		 */
		button_invmodeget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { -1 };

				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE, val);

				if (er == READER_ERR.MT_OK_ERR) {
					spinner_invmode.setSelection(val[0]);
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
			}

		});

		/**
		 * 设置盘点搜索模式
		 */
		button_invmodeset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int[] val = new int[] { spinner_invmode
						.getSelectedItemPosition() };
				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE, val);
				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.invw = spinner_invmode
							.getSelectedItemPosition();
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		button_6bdpget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_unsupport, Toast.LENGTH_SHORT)
						.show();

			}

		});

		button_6bdpset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_unsupport, Toast.LENGTH_SHORT)
						.show();
			}

		});

		button_6bdltget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_unsupport, Toast.LENGTH_SHORT)
						.show();

			}

		});

		button_6bdltset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_unsupport, Toast.LENGTH_SHORT)
						.show();
			}

		});

		/**
		 * 获取6b blf
		 */
		button_6bblfget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int[] val = new int[] { -1 };

				READER_ERR er = myapp.Mreader.ParamGet(
						Mtr_Param.MTR_PARAM_POTL_ISO180006B_BLF, val);

				if (er == READER_ERR.MT_OK_ERR) {
					EditText et = (EditText) findViewById(R.id.editText_6bblf);
					et.setText(String.valueOf(val[0]));
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_GetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();

			}

		});

		/**
		 * 设置6b blf
		 */
		button_6bblfset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					EditText et = (EditText) findViewById(R.id.editText_6bblf);

					int[] val = new int[] { Integer.valueOf(et.getText()
							.toString()) };
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_POTL_ISO180006B_BLF, val);
					if (er == READER_ERR.MT_OK_ERR)
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

		});

		/**
		 * 获取温度
		 */
		button_gettempture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					EditText et = (EditText) findViewById(R.id.editText_tempure);

					int[] val = new int[1];
					val[0] = 0;
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_RF_TEMPERATURE, val);
					if (er == READER_ERR.MT_OK_ERR) {
						et.setText(String.valueOf(val[0]));

					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_GetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

		});

		/**
		 * 配置快速模式
		 */
		button_quicklyread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				int metaflag = 0;
				myapp.m_BROption.TMFlags.IsEmdData = false;
				myapp.m_BROption.TMFlags.IsReadCnt = false;
				myapp.m_BROption.TMFlags.IsRSSI = false;
				myapp.m_BROption.TMFlags.IsAntennaID = false;
				myapp.m_BROption.TMFlags.IsFrequency = false;
				myapp.m_BROption.TMFlags.IsTimestamp = false;
				myapp.m_BROption.TMFlags.IsRFU = false;
				myapp.m_BROption.TMFlags.IsEmdData = false;
				if (cbmf_readcount.isChecked()) {
					metaflag |= 0X0001;
					myapp.m_BROption.TMFlags.IsReadCnt = true;
				}
				if (cbmf_rssi.isChecked()) {
					metaflag |= 0X0002;
					myapp.m_BROption.TMFlags.IsRSSI = true;
				}
				if (cbmf_ant.isChecked()) {
					metaflag |= 0X0004;
					myapp.m_BROption.TMFlags.IsAntennaID = true;
				}
				if (cbmf_fre.isChecked()) {
					metaflag |= 0X0008;
					myapp.m_BROption.TMFlags.IsFrequency = true;
				}
				if (cbmf_time.isChecked()) {
					metaflag |= 0X0010;
					myapp.m_BROption.TMFlags.IsTimestamp = true;
				}
				if (cbmf_rfu.isChecked()) {
					metaflag |= 0X0020;
					myapp.m_BROption.TMFlags.IsRFU = true;
				}
				if (cbmf_pro.isChecked()) {
					metaflag |= 0X0040;

				}
				if (cbmf_dl.isChecked()) {
					metaflag |= 0X0080;
					myapp.m_BROption.TMFlags.IsEmdData = true;
				}
				myapp.Rparams.option =0;
				//0-10 0%-50%
				myapp.Rparams.option = (metaflag << 8)
						| spinner_persen.getSelectedItemPosition();
 
				 
				myapp.issmartmode=false;
				if (spinner_qmode.getSelectedItemPosition()==-1||
						spinner_qmode.getSelectedItemPosition()==0)
				{	myapp.isquicklymode = false;
				
				}
				else
				{
					myapp.Rparams.sleep = 0;
					myapp.isquicklymode = true;
				    
					int[] mp = new int[1];
					 String msg="";
					READER_ERR er = myapp.Mreader.ParamGet(
							Mtr_Param.MTR_PARAM_RF_MAXPOWER, mp);
					if (er == READER_ERR.MT_OK_ERR) {
						
						 msg+="PowMax "+ String.valueOf((short) mp[0])+",";
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
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
						if (er == READER_ERR.MT_OK_ERR)
						{
							myapp.Rparams.rpow =  rpow;
							myapp.Rparams.wpow =  wpow;
							
						}
					}
					
					if (spinner_qmode.getSelectedItemPosition()==1)
					{
						 msg+="Session 0,";
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 0 });
						if (er == READER_ERR.MT_OK_ERR)
							myapp.Rparams.session = 0;
						
						if (myapp.myhd.module==Module_Type.MODOULE_SLR1200)
						{
							myapp.Rparams.option |=0x10;
							 msg+="HM";
						}
					}
					else if (spinner_qmode.getSelectedItemPosition()==2)
					{
						msg+="Session 1";
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 1 });
						if (er == READER_ERR.MT_OK_ERR)
							myapp.Rparams.session = 1;
					}
					else if (spinner_qmode.getSelectedItemPosition()==3)
					{
						msg+="Session 1,";
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 1 });
						if (er == READER_ERR.MT_OK_ERR)
							myapp.Rparams.session = 1;
						
						if (myapp.myhd.module==Module_Type.MODOULE_SLR1200)
						{
							myapp.Rparams.option |=0x10;
							 msg+="HM";
						}
							 
					}
					else if (spinner_qmode.getSelectedItemPosition()==4)
					{
						msg+="Session 1,";
						er = myapp.Mreader.ParamSet(
								Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[] { 1 });
						if (er == READER_ERR.MT_OK_ERR)
							myapp.Rparams.session = 1;
						
						if (myapp.myhd.module==Module_Type.MODOULE_SLR1200)
						{
							myapp.Rparams.option |=0x10;
							 msg+="HM,";
						}
						 msg+="Smart,盘点控制 循环将不可用";
						myapp.issmartmode=true;
						myapp.smartmode=Reader.IT_MODE.IT_MODE_CT;
					}
					else if (spinner_qmode.getSelectedItemPosition()==5)
					{
						myapp.issmartmode=true;
						myapp.smartmode=Reader.IT_MODE.IT_MODE_S2;
					}
					else if (spinner_qmode.getSelectedItemPosition() == 6) {
						myapp.smartmode=Reader.IT_MODE.IT_MODE_E7;
						myapp.issmartmode = true;
					}
					else if (spinner_qmode.getSelectedItemPosition() == 7) {
						myapp.smartmode=Reader.IT_MODE.IT_MODE_E7v2;
						myapp.issmartmode = true;
					}
					
					
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_set + msg,
							Toast.LENGTH_SHORT).show();
				}
				
				MainActivity.spinner_qmode.setSelection(Sub4TabActivity.spinner_qmode.getSelectedItemPosition());
			}
		});

		
		/**
		 * u8标签设置
		 */
		button_nxpu8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// 设置使能或禁用u8特殊盘点功能(set eanble or disenable inventory u8 tag
				// functions)
				// *
				NXP_U8_InventoryModePara u8para = myapp.Mreader.new NXP_U8_InventoryModePara();

				u8para.Mode[0] = 0;
				myapp.nxpu8 = 0;

				READER_ERR er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAG_FILTER, null);
				if (er == READER_ERR.MT_OK_ERR) {

				}

				er = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, null);
				if (er == READER_ERR.MT_OK_ERR) {

				}

				if (commfun.SortGroup(rg_nxpu8) == 0)
					;
				else if (commfun.SortGroup(rg_nxpu8) == 1) {

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
				} else if (commfun.SortGroup(rg_nxpu8) == 2) {
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
				} else if (commfun.SortGroup(rg_nxpu8) == 3) {
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
				// */
			}
		});

		/**
		 * 显示方式
		 */
		button_showay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (cb_showuinant.isChecked()) {
					myapp.isUniByAnt = true;
				} else
					myapp.isUniByAnt = false;

				if (cb_showuinemd.isChecked()) {
					myapp.isUniByEmd = true;
				} else
					myapp.isUniByEmd = false;
				
				if (cb_epcup.isChecked()) {
					myapp.isEpcup = true;
				} else
					myapp.isEpcup = false;
			}
		});

		/**
		 * 统一功率设置
		 */
		button_allpwset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				int[] rp = new int[16];
				int[] wp = new int[16];
				EditText allp = (EditText) findViewById(R.id.editText_allpw);
				for (int i = 0; i < 16; i++) {
					rp[i] = Integer.parseInt(allp.getText().toString());
					wp[i] = rp[i];
				}

				AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
				apcf.antcnt = myapp.antportc;
				int[] rpow = new int[apcf.antcnt];
				int[] wpow = new int[apcf.antcnt];
				for (int i = 0; i < apcf.antcnt; i++) {
					AntPower jaap = myapp.Mreader.new AntPower();
					jaap.antid = i + 1;
					jaap.readPower = (short) (rp[i]);
					rpow[i] = jaap.readPower;

					jaap.writePower = (short) (wp[i]);
					wpow[i] = jaap.writePower;
					apcf.Powers[i] = jaap;
				}

				try {
					READER_ERR er = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
					if (er == READER_ERR.MT_OK_ERR) {
						myapp.Rparams.rpow = rpow;
						myapp.Rparams.wpow = wpow;
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + er.toString(),
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
         
		button_timecontrol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				myapp.isstoptime=((CheckBox) findViewById(R.id.checkBox_chkstoptime)).isChecked();
				myapp.isstopcount=((CheckBox) findViewById(R.id.checkBox_chkstopcount)).isChecked();
				try{
				 
					if(myapp.isstoptime)
				myapp.stoptimems=Long.parseLong(((EditText) findViewById(R.id.editText_stoptime)).getText().toString()); 
				    if(myapp.isstopcount)
					myapp.stopcount=Integer.parseInt(((EditText) findViewById(R.id.editText_stopcount)).getText().toString());}
				catch(Exception ex)
				{
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				myapp.islatetime=((CheckBox) findViewById(R.id.checkBox_chklatetime)).isChecked();
				try{
				 
				myapp.latetimems=Long.parseLong(((EditText) findViewById(R.id.editText_latetime)).getText().toString());}
				catch(Exception ex)
				{
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_setcep + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				try {
					CheckBox cb = (CheckBox) findViewById(R.id.checkBox_autostopcount);
					myapp.VisTestFor=cb.isChecked();
					if (myapp.VisTestFor) {
						EditText et_autostoptimecount=(EditText)findViewById(R.id.editText_autostopcount);
						EditText et_testforsleep=(EditText)findViewById(R.id.editText_testforsleep);
						myapp.Vtestforcount = Integer.parseInt(et_autostoptimecount
								.getText().toString());
						myapp.Vtestforsleep = Integer.parseInt(et_testforsleep
								.getText().toString());
					}
				} catch (Exception ex) {

				}
				
				
				if(myapp.isstoptime||myapp.isstopcount)
					myapp.VisStatics=true;
				else
					myapp.VisStatics=false;
				
				Toast.makeText(Sub4TabActivity.this,
						MyApplication.Constr_ok,
						Toast.LENGTH_SHORT).show();
			}
		});
	 
		
		button_report_sec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
					//CheckBox cb = (CheckBox) findViewById(R.id.checkBox_chkstatics);
					//myapp.VisStatics=cb.isChecked();
					
					 if(cb_report_rec.isChecked()){
						 
						 /** txt文件
							try{
								//"/sdcard/testrecord.txt");
								
					           myapp.file = new File(Environment.getExternalStorageDirectory().getPath()+"/时间数量报表.txt");
					            if(myapp.file.exists())
					            	myapp.fs=new FileOutputStream(myapp.file,true);
					            else
					            {
					            	myapp.fs = new FileOutputStream(myapp.file);
					            String wstr = "Test count,per sec/unique/add/total count,cost time\r\n";
					            myapp.fs.write(wstr.getBytes());}
					          }catch (Exception ex)
					         {
					            Toast.makeText(Sub4TabActivity.this, ex.getMessage(), Toast.LENGTH_SHORT)
					                    .show();
					         }
					         */
							 myapp.isReport_rec = true; 
					 }
					 else
						 myapp.isReport_rec = false; 
					 
					 if (cb_report_pos.isChecked()) {
							myapp.isReport_pos = true;
						} else
							myapp.isReport_pos = false;
					 
					 if (cb_report_tep.isChecked()) {
							myapp.isReport_tep = true;
						} else
							myapp.isReport_tep = false;
					 Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_ok,
								Toast.LENGTH_SHORT).show();
			}
		});
		 
		button_fastidset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(cb_fastid.isChecked())
				{
					try {
						
						Reader.CustomParam_ST cpara = myapp.Mreader.new CustomParam_ST();
						cpara.ParamName = "tagcustomcmd/fastid";
						cpara.ParamVal = new byte[1];
						cpara.ParamVal[0] = 1;
						READER_ERR ret = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpara);
						
						
						myapp.isFastID=true;
						if (ret == READER_ERR.MT_OK_ERR) {
						
							Toast.makeText(Sub4TabActivity.this,
									MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
									.show();
						} else
							Toast.makeText(Sub4TabActivity.this,
									MyApplication.Constr_SetFaill + ret.toString(),
									Toast.LENGTH_SHORT).show();

					} catch (Exception ex) {
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_setcep + ex.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					//READER_ERR er = myapp.Mreader.ParamSet(
						//	Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, null);
					Reader.CustomParam_ST cpara = myapp.Mreader.new CustomParam_ST();
					cpara.ParamName = "tagcustomcmd/fastid";
					cpara.ParamVal = new byte[1];
					cpara.ParamVal[0] = 0;
					READER_ERR ret = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpara);
					myapp.isFastID=false;
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				}
			}
			
		});
	 
		button_tagfoucs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(cb_tagfoucs.isChecked()) {
					Reader.CustomParam_ST cpara = myapp.Mreader.new CustomParam_ST();
					cpara.ParamName = "tagcustomcmd/tagfocus";
					cpara.ParamVal = new byte[1];
					cpara.ParamVal[0] = 1;
					READER_ERR ret = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpara);

					if (ret == READER_ERR.MT_OK_ERR) {
						myapp.isTagfoucs = true;
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + ret.toString(),
								Toast.LENGTH_SHORT).show();
				}
				else {
					Reader.CustomParam_ST cpara = myapp.Mreader.new CustomParam_ST();
					cpara.ParamName = "tagcustomcmd/tagfocus";
					cpara.ParamVal = new byte[1];
					cpara.ParamVal[0] = 0;
					READER_ERR  ret = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpara);

					if (ret == READER_ERR.MT_OK_ERR) {
						myapp.isTagfoucs = false;
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
								.show();
					} else
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + ret.toString(),
								Toast.LENGTH_SHORT).show();
				}

			}
			
			
		});

		button_tagfound.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (cb_tagfound.isChecked()) {
					if(myapp.Curepc==null||myapp.Curepc.isEmpty())
					{
						Toast.makeText(Sub4TabActivity.this,
								MyApplication.Constr_SetFaill + " no tag selected",
								Toast.LENGTH_SHORT).show();
						return;
					}
					myapp.isFoundTag = true;
					myapp.issound=false;
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_ok,
							Toast.LENGTH_SHORT).show();
				}
				else {
					myapp.isFoundTag = false;
					myapp.issound=true;
				}
			}
		});

		button_smartparset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Object[] objs=new Object[5];
				EditText et1=(EditText)findViewById(R.id.editText_smm1);
				EditText et2=(EditText)findViewById(R.id.editText_smm2);
				EditText et3=(EditText)findViewById(R.id.editText_smm3);
				EditText et4=(EditText)findViewById(R.id.editText_smm4);
				EditText et5=(EditText)findViewById(R.id.editText_smm5);
				int p=0;
				
				try {
					objs[p++]=Integer.parseInt(et1.getText().toString());
					objs[p++]=Integer.parseInt(et2.getText().toString());
					objs[p++]=Integer.parseInt(et3.getText().toString());
					objs[p++]=Integer.parseInt(et4.getText().toString());
					objs[p++]=Integer.parseInt(et5.getText().toString());
					myapp.Mreader.Set_IT_Params(IT_MODE.IT_MODE_CT, objs);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

				Object[] objs2=new Object[4];
				EditText ets2_1=(EditText)findViewById(R.id.editText_s2m_cyc1);
				EditText ets2_2=(EditText)findViewById(R.id.editText_s2m_tagc1);
				EditText ets2_3=(EditText)findViewById(R.id.editText_s2m_cyc2);
				EditText ets2_4=(EditText)findViewById(R.id.editText_s2m_tagc2);

				 p=0;

				try {
					objs2[p++]=Integer.parseInt(ets2_1.getText().toString());
					objs2[p++]=Integer.parseInt(ets2_2.getText().toString());
					objs2[p++]=Integer.parseInt(ets2_3.getText().toString());
					objs2[p++]=Integer.parseInt(ets2_4.getText().toString());

					myapp.Mreader.Set_IT_Params(IT_MODE.IT_MODE_S2, objs2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

				Object[] objs3=new Object[5];
				EditText ets3_1=(EditText)findViewById(R.id.editText_s2m_rfmode);

				p=0;

				try {
					objs3[p++]=Integer.parseInt(ets2_1.getText().toString());
					objs3[p++]=Integer.parseInt(ets2_2.getText().toString());
					objs3[p++]=Integer.parseInt(ets2_3.getText().toString());
                    objs3[p++]=Integer.parseInt(ets2_4.getText().toString());
					objs3[p++]=Integer.parseInt(ets3_1.getText().toString());
					myapp.Mreader.Set_IT_Params(IT_MODE.IT_MODE_E7, objs3);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		button_gpodemo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myapp.gpodemomode=spinner_gpodemo.getSelectedItemPosition();
				if(myapp.gpodemomode>2)
				{
					
					if(cb_gpo1.isChecked())
						myapp.gpodemo1=true;
					else
						myapp.gpodemo1=false;
					if(cb_gpo2.isChecked())
						myapp.gpodemo2=true;
					else
						myapp.gpodemo2=false;
					
					if(cb_gpo3.isChecked())
						myapp.gpodemo3=true;
					else
						myapp.gpodemo3=false;
					if(cb_gpo4.isChecked())
						myapp.gpodemo4=true;
					else
						myapp.gpodemo4=false;
				}
				try{
					EditText et=(EditText)findViewById(R.id.editText_gpodemo);
					String valstr=et.getText().toString();
					myapp.gpodemotime=Integer.parseInt(valstr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

		});

		button_hopantime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try{
				EditText atval=(EditText)findViewById(R.id.editText_antime);
				int vl=Integer.parseInt(atval.getText().toString());
				READER_ERR er =myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_RF_HOPANTTIME,new int[]{vl});

				if (er == READER_ERR.MT_OK_ERR) {
					myapp.Rparams.checkant = commfun
							.SortGroup(rg_antcheckenable);
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetOk, Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + er.toString(),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(Sub4TabActivity.this,
							MyApplication.Constr_SetFaill + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
	    });
		cb_allsel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				MyAdapter_fre m2 = (MyAdapter_fre) elist.getAdapter();
				if (arg1 == true) {
					HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

					for (int m = 0; m < m2.getCount(); m++)
						isSelected.put(m, true);
					m2.setIsSelected(isSelected);

				} else {
					HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

					for (int m = 0; m < m2.getCount(); m++)
						isSelected.put(m, false);
					m2.setIsSelected(isSelected);
				}
				elist.setAdapter(m2);
			}

		});
		tabHost_set.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				int j = tabHost_set.getCurrentTab();
				TabWidget tabIndicator = tabHost_set.getTabWidget();
				View vw = tabIndicator.getChildAt(j);
				vw.setBackgroundColor(Color.BLUE);
				int tc = tabHost_set.getTabContentView().getChildCount();
				for (int i = 0; i < tc; i++) {
					if (i != j) {
						View vw2 = tabIndicator.getChildAt(i);
						vw2.setBackgroundColor(Color.TRANSPARENT);
					} else {
						switch (i) {
						case 0:

							button_getusl.performClick();
							button_opproget.performClick();
							break;
						case 1:
							button_getantcheck.performClick();
							button_getantpower.performClick();
							break;
						case 2:
							button_getreg.performClick();
							button_getfre.performClick();
							break;
						case 3:
							button_getgen2ses.performClick();
							button_getgen2q.performClick();
							button_getwmode.performClick();
							// button_getgen2blf.performClick();
							button_getgen2maxl.performClick();
							button_getgen2targ.performClick();
							button_getgen2code.performClick();
							// button_getgen2tari.performClick();
							break;
						case 4:
							button_getfil.performClick();
							break;
						case 5:
							button_getemd.performClick();
							break;
						case 7:
							button_oantuqget.performClick();
							button_odatauqget.performClick();
							button_hrssiget.performClick();
							break;
						}
					}
				}

			}

		});

		cb_gen2.setChecked(false);
		cb_6b.setChecked(false);
		cb_ipx64.setChecked(false);
		cb_ipx256.setChecked(false);
		for (int k = 0; k < myapp.Rparams.invpro.size(); k++) {
			if (myapp.Rparams.invpro.get(k).equals("GEN2"))
				cb_gen2.setChecked(true);
			else if (myapp.Rparams.invpro.get(k).equals("6B"))
				cb_6b.setChecked(true);
			else if (myapp.Rparams.invpro.get(k).equals("IPX64"))
				cb_ipx64.setChecked(true);
			else if (myapp.Rparams.invpro.get(k).equals("IPX256"))
				cb_ipx256.setChecked(true);
		}

		cb_ant1.setChecked(false);
		cb_ant2.setChecked(false);
		cb_ant3.setChecked(false);
		cb_ant4.setChecked(false);
		cb_ant5.setChecked(false);
		cb_ant6.setChecked(false);
		cb_ant7.setChecked(false);
		cb_ant8.setChecked(false);
		cb_ant9.setChecked(false);
		cb_ant10.setChecked(false);
		cb_ant11.setChecked(false);
		cb_ant12.setChecked(false);
		cb_ant13.setChecked(false);
		cb_ant14.setChecked(false);
		cb_ant15.setChecked(false);
		cb_ant16.setChecked(false);
		for (int k = 0; k < myapp.Rparams.uants.length; k++) {
			if (myapp.Rparams.uants[k] == 1) {
				cb_ant1.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 2) {
				cb_ant2.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 3) {
				cb_ant3.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 4) {
				cb_ant4.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 5) {
				cb_ant5.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 6) {
				cb_ant6.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 7) {
				cb_ant7.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 8) {
				cb_ant8.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 9) {
				cb_ant9.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 10) {
				cb_ant10.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 11) {
				cb_ant11.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 12) {
				cb_ant12.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 13) {
				cb_ant13.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 14) {
				cb_ant14.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 15) {
				cb_ant15.setChecked(true);
			} else if (myapp.Rparams.uants[k] == 16) {
				cb_ant16.setChecked(true);
			}

		}
		this.button_getusl.performClick();
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
