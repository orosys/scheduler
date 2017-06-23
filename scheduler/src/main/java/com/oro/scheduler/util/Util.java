package com.oro.scheduler.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import com.oro.scheduler.AbstractEventDispatcherService;


/**
 * Created by oro on 15. 7. 17..
 */
public class Util {
	public static Class<? extends AbstractEventDispatcherService> getEventDispatcherService(Context context) {
		final PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo;
		try {
			pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
			for (ServiceInfo serviceInfo : pkgInfo.services) {
				try {
					Class<?> clazz = Class.forName(serviceInfo.name);
					if (typeOf(clazz, AbstractEventDispatcherService.class)) {
						return (Class<? extends AbstractEventDispatcherService>) clazz;
					}
				} catch (ClassNotFoundException e) {
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean typeOf(Class<?> clazz, Class<?> target) {
		Class<?> superClazz = clazz;
		while (!superClazz.getName().equals(target.getName())) {
			if (superClazz.getName().equals(Object.class.getName())) {
				return false;
			}
			superClazz = superClazz.getSuperclass();
		}
		return true;
	}
}
