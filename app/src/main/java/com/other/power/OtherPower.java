package com.other.power;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.content.Context;
import android.content.Intent;

import com.idata.serialport.SerialPortControl;
import com.tools.dlog;
import com.znht.iodev2.PowerCtl;
//import android.app.*;


public class OtherPower {
   // 上层接口，在接口内实现具体的手持机平台上电，掉电方法
   // public static boolean isotherpowerup=false;

	public static Context Ctt;
	public static boolean oPowerUp()
	{
		boolean rebl=true;
		//power_up();
		//new OtherPower().panningpowerup();
		//power_jingrui(true);
		//panning2_up();
		//nn_powerup();
		//return idata_95w_powerup();
		//handleRfidPower(true);

		rebl=ydpowerup();

		return rebl;
	}

	public static boolean oPowerDown()
	{
		boolean rebl=true;
		//power_down();
		//power_jingrui(false);
		//panning2_down();
		//nn_powerdown();
		//return idata_95w_powerdown();
		//handleRfidPower(false);

		rebl=ydpowerdown();
		return rebl;
	}



	//各手持机平台电源管理，需定义OpAddr地址
	//public static String OpAddr="/dev/ttysWK1";//鸟鸟
	/*public static UHFManager mUHFManager;
	public  static void nn_init()
	{
		mUHFManager = new UHFManager(Ctt.getApplicationContext());
	}
	public  static void nn_powerup()
	{
		mUHFManager.controlUHFPower(true,1);
	}
	public  static  void nn_powerdown()
	{
		mUHFManager.controlUHFPower(false,1);
	}
	*/

	/*
	public  static PowerControl nnpower=new PowerControl();
	public  static void nn_powerup()
	{
		nnpower._5VPowerControl(true);
	}
	public  static  void nn_powerdown()
	{
		nnpower._5VPowerControl(false);
	}
*/


	//idata 95w 10
	//public static String OpAddr="/dev/ttyS0";
	public static boolean idata_95w_powerup(){
		boolean bl= com.idata.rfid.SerialPortControl.controlIO("/dev/idata_uhf",0x05);
		try {
			Thread.sleep(1000);
		}catch (Exception ex)
		{}
		return  bl;
	}
	public static boolean idata_95w_powerdown()
	{
		return com.idata.rfid.SerialPortControl.controlIO("/dev/idata_uhf",0x06);
	}

	
	
	//肖邦新手持机上电？？？
	private void power_up() {
		   File com = new File("/sys/devices/platform/odm/odm:exp_dev/gps_com_switch");
		   File power = new File("/sys/bus/platform/devices/odm:exp_dev/psam_en");
		   dlog.toDlogAPI("power:" + power);
		  
		   writeFile(com, "1");
		   writeFile(power, "1");
		}
	
	private void power_down() {
		   File com = new File("/sys/devices/platform/odm/odm:exp_dev/gps_com_switch");
		   File power = new File("/sys/bus/platform/devices/odm:exp_dev/psam_en");
		   dlog.toDlogAPI("power:" + power);
		  
		   writeFile(com, "0");
		   writeFile(power, "0");
		}
	private synchronized void writeFile(File file, String value) {

		   try {
		      FileOutputStream outputStream = new FileOutputStream(file);
		      outputStream.write(value.getBytes());
		      outputStream.flush();
		      outputStream.close();

		   } catch (FileNotFoundException e) {
		      e.printStackTrace();
		   } catch (IOException e) {
		      e.printStackTrace();
		   }
		}
	//>>>>>>>>>>>>>>>>>>>>>>>

   //攀宁1上电
	// public static String OpAddr="/dev/ttysWK0";
	//*
	private PowerCtl powerCtl ;
	public void panningpowerup() {

		try {
			powerCtl = new PowerCtl();
			powerCtl.psam_ctl(1);
			powerCtl.sub_board_power(1);
			powerCtl.identity_ctl(1);
			powerCtl.scan_trig(1);
			powerCtl.scan_wakeup(0);
			powerCtl.scan_power(1);
			powerCtl.identity_uhf_power(1);
			powerCtl.uhf_ctl(1);

		} catch (SecurityException e) {
			//DisplayError(R.string.error_security);
		} catch (InvalidParameterException e) {
			//DisplayError(R.string.error_configuration);
		}
	}
	//*/
	
	//攀宁2
	/*public static String OpAddr="/dev/ttysWK2";//攀宁
	 private static PowerCtl powerCtl = new PowerCtl();
	private static void panning2_up()
	{
		powerCtl.identity_uhf_power(1);
		powerCtl.identity_ctl(1);
		powerCtl.uhf_ctl(1);
	}
	private static void panning2_down()
	{

		powerCtl.identity_uhf_power(0);
		powerCtl.identity_ctl(0);
		powerCtl.uhf_ctl(0);
	}
	 */
	
	//晶锐手持机上电
	//public static String OpAddr="/dev/ttyMT1";//晶锐
	public static final String KEY_RFIDIG_POWER_ACTION = "android.intent.action.RfidIgPower";
	private static void power_jingrui(boolean enable)
	{
		Intent intent = new Intent(KEY_RFIDIG_POWER_ACTION);
		intent.putExtra(KEY_RFIDIG_POWER_ACTION, enable);
		Ctt.sendBroadcast(intent);
	}

	//东方拓宇 IWRIST R11
	//public static String OpAddr="/dev/ttyS1";
	private static void handleRfidPower(boolean powerOn) {
		Intent pintent = new Intent("android.intent.action.SETTINGS_BJ");
		pintent.putExtra( "enable" , powerOn);
		Ctt.sendBroadcast(pintent);
	}


	//盈达 210802
	public static String OpAddr= "/dev/ttyS1";
	private static boolean ydpowerup()
	{
		return com.idata.serialport.SerialPortControl.ioControl("/dev/uhf_dev",1);
	}
	private static boolean ydpowerdown()
	{
		return com.idata.serialport.SerialPortControl.ioControl("/dev/uhf_dev",0);
	}
}
