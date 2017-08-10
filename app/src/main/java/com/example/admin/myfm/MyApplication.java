package com.example.admin.myfm;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class MyApplication extends Application {

//	public List<String> songsList;//当前播放列表
//	public int songItemPos;//当前播放音乐在列表中的位置
//	public NotificationManager notManager;
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoder(this);
//		notManager = (NotificationManager) getSystemService
//				(this.NOTIFICATION_SERVICE);
//		Intent intent = new Intent(this, ServiceBroadCast.class);
//		startService(intent);
	}

	/**
	 * 初始化ImageLoder的方法
	 *
	 * @param context
	 */
	private void initImageLoder(Context context) {
		// 设置缓存图片的路径
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"imageloader/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				// 设置当前线程的优先级
				context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// 缓存显示不同大小的同一张图片
				.denyCacheImageMultipleSizesInMemory()
				// .discCacheFileNameGenerator(new
				// Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				// 50 Mb sd卡(本地)缓存的最大值
				.diskCacheSize(50 * 1024 * 1024)
				// sd卡缓存
				.diskCache(new UnlimitedDiscCache(cacheDir))
				// 内存缓存
				.memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		/*
		 * getInstance单例模式：在内存中只能存在一个对象 构造方法私有
		 */
		ImageLoader.getInstance().init(config);
	}
	private double latitude = 0.0;
	private double longitude = 0.0;
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 获取经纬度
	 */
	public void checkLocation() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		} else {
			LocationListener locationListener = new LocationListener() {
				// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数  
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
				}
				// Provider被enable时触发此函数，比如GPS被打开  
				@Override
				public void onProviderEnabled(String provider) {
				}
				// Provider被disable时触发此函数，比如GPS被关闭   
				@Override
				public void onProviderDisabled(String provider) {
				}

				//当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发   
				@Override
				public void onLocationChanged(Location location) {
					if (location != null) {
						Log.e("Map", "Location changed : Lat: "
								+ location.getLatitude() + " Lng: "
								+ location.getLongitude());
					}
				}
			};

			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(location != null){
				latitude = location.getLatitude(); //经度     
				longitude = location.getLongitude(); //纬度  
			}
		}
	}
}
