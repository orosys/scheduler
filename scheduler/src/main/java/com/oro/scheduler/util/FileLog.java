package com.oro.scheduler.util;

import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by oro on 15. 2. 5..
 * csv format
 * date{yyyy-MM-dd HH:mm:ss},skd kind{ble,geo,wifi},error type{v,d,i,w,e,a},tag{request,response,device,log,system},data
 */
public class FileLog {
	private static final String TAG = FileLog.class.getSimpleName();
	private static final String FILE_LOG = "odin_log";
	private static final long FILE_SIZE = 50 * 1024 * 1024;    // file.log file size = 5mb
	private static final double FILE_SIZE_BUFFER = FILE_SIZE * 0.7;    // 70%
	private static final int MAX_FILE_COUNT = 5;

	public static void v(String tag, String data) {
		data = appendTag(tag, data);
		data = appendType("v", data);
		data = appendDate(data);
		save(data);
	}

	public static void d(String tag, String data) {
		data = appendTag(tag, data);
		data = appendType("d", data);
		data = appendDate(data);
		save(data);
	}

	public static void i(String tag, String data) {
		data = appendTag(tag, data);
		data = appendType("i", data);
		data = appendDate(data);
		save(data);
	}

	public static void w(String tag, String data) {
		data = appendTag(tag, data);
		data = appendType("w", data);
		data = appendDate(data);
		save(data);
	}

	public static void e(String tag, String data) {
		data = appendTag(tag, data);
		data = appendType("e", data);
		data = appendDate(data);
		save(data);
	}

	public static File getLogFile() {
		File dir = Environment.getExternalStorageDirectory();
		String model = FILE_LOG + "." + Build.MODEL + ".txt";
		File file = new File(dir, model);
		return file;
	}

	private static void save(String data) {
		File file = getLogFile();
		try {
			FileWriter out = new FileWriter(file, true);
			out.write(data + "\n");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (file.length() > FILE_SIZE) {
			//removeLineFromFile(file, FILE_SIZE_BUFFER);
			renameFile(file);
		}
	}

	private static String appendDate(String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
		String now = sdf.format(new Date());
		return now + "," + data;
	}

	private static String appendType(String type, String data) {
		return type + "," + data;
	}

	private static String appendKind(String kind, String data) {
		return kind + "," + data;
	}

	private static String appendTag(String tag, String data) {
		return tag + "," + data;
	}

	private static void renameFile(File file) {
		String oriFileName = file.getName();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.KOREA);
		String now = sdf.format(new Date());

		String model = FILE_LOG + "." + Build.MODEL;
		String newName = model + "_" + now + ".txt";
		newName = newName.replace(" ", "_");
		String newFilePath = file.getAbsolutePath().replace(file.getName(), "") + newName;
		File newFile = new File(newFilePath);

		boolean isSuccess = false;
		if (file.exists()) {
			isSuccess = file.renameTo(newFile);
		}

		if (isSuccess) {
			File logDir = new File(file.getParent());
			File files[] = logDir.listFiles();

			Arrays.sort(files, new Comparator<File>() {
				public int compare(File strA, File strB) {
					return (strA.getName()).compareToIgnoreCase(strB.getName());
				}
			});

			List<File> logFileList = new ArrayList<File>();
			for (File f : files) {
				if (f.getName().startsWith(model) && !oriFileName.equals(f.getName())) {
					logFileList.add(f);
				}
			}

			while (logFileList.size() > MAX_FILE_COUNT) {
				File removeFile = logFileList.get(0);
				removeFile.delete();
				logFileList.remove(removeFile);
			}
		}
	}

	private static void removeLineFromFile(File inFile, double cutLineSize) {
		try {
			if (!inFile.isFile()) {
				System.out.println("Parameter is not an existing file");
				return;
			}

			//Construct the new file that will later be renamed to the original filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(inFile.getAbsolutePath()));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			//Read from the original file and write to the new
			//unless content matches data to be removed.
			long deleteLineSize = inFile.length();
			while ((line = br.readLine()) != null) {
				deleteLineSize -= line.getBytes().length;

				if (deleteLineSize < cutLineSize) {
					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			//Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return;
			}

			//Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
