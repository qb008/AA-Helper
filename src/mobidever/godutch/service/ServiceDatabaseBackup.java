package mobidever.godutch.service;

import java.util.Date;

import mobidever.godutch.business.BusinessDataBackup;
import mobidever.godutch.receiver.ReceiverDatabaseBackup;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceDatabaseBackup extends Service {

//	private static final long SPACINGIN_TERVAL = 48*60*60*1000;
	
	private static final long SPACINGIN_TERVAL = 10000;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		BusinessDataBackup _BusinessDataBackup = new BusinessDataBackup(this);
		long _BackupMillise = _BusinessDataBackup.LoadDatabaseBackupDate();
		Date _BackupDate = new Date();
		if (_BackupMillise == 0) {
			_BusinessDataBackup.DatabaseBackup(_BackupDate);
			_BackupMillise = _BusinessDataBackup.LoadDatabaseBackupDate();
		}
		else {
			if(_BackupDate.getTime() - _BackupMillise >= SPACINGIN_TERVAL)
			{
				_BusinessDataBackup.DatabaseBackup(_BackupDate);
				_BackupMillise = _BusinessDataBackup.LoadDatabaseBackupDate();
			}
		}
		
		Log.i("GoDutch", "服务：" + (_BackupMillise/1000) + "");
		Intent _Intent = new Intent(this,ReceiverDatabaseBackup.class);
		_Intent.putExtra("Date", _BackupMillise);
		PendingIntent _PI = PendingIntent.getBroadcast(this, 0, _Intent,PendingIntent.FLAG_ONE_SHOT);
        //设置一个PendingIntent对象，发送广播
        AlarmManager _AM=(AlarmManager)getSystemService(ALARM_SERVICE);
        //获取AlarmManager对象
        _AM.set(AlarmManager.RTC_WAKEUP, _BackupMillise + SPACINGIN_TERVAL, _PI);
	}
}
