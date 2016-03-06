package com.lpoezy.nexpa.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

public class HttpUtilz {

	public static String makeRequest(final String spec, final HashMap<String, String> postDataParams) {

		String webPage = "", data = "";

		Random random = new Random();
		int rInt = random.nextInt(5);
		for (int i = 0; i < 5; i++) {

			int delay = (2 << i);
			try {
				Thread.sleep((1000 * delay) + rInt);
			} catch (InterruptedException e) {
			}

			try {
				URL url = new URL(spec);
				HttpURLConnection conn = null;

				try {
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(10000);
					conn.setRequestMethod("POST");
					conn.setDoInput(true);
					conn.setDoOutput(true);
					
					OutputStream os = conn.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

					writer.write(getPostDataString(postDataParams));
					writer.flush();
					writer.close();
					os.close();

					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

						while ((data = reader.readLine()) != null) {
							webPage += data + "\n";
						}

						break;
					}

				} catch (IOException e) {
					L.error("" + e);
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}

			} catch (MalformedURLException e) {
				L.error("" + e);
			}

		}

		return webPage;

	}

	public static Bitmap downloadImage(String spec) {

		Random random = new Random();
		int rInt = random.nextInt(5);
		Bitmap img = null;

		for (int i = 0; i < 5; i++) {

			int delay = (2 << i);
			try {
				Thread.sleep((1000 * delay) + rInt);
			} catch (InterruptedException e) {
			}

			try {

				L.debug("start file download ,delay "+delay);
				URL url = new URL(spec); // you can write here any link
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);

				if (conn.getResponseCode() == 200) {

					try {
						InputStream is = conn.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);

						img = BitmapFactory.decodeStream(bis);
						
					} finally {
						if (conn != null) {
							conn.disconnect();
						}
					}
					L.debug("file download complete");
					break;
				}

			} catch (IOException e) {
				L.error("" + e);
			}

		}

		return img;
	}

	public static String downloadFileFrmUrl(String spec, String dir) {

		Random random = new Random();
		int rInt = random.nextInt(5);
		for (int i = 0; i < 5; i++) {

			int delay = (2 << i);
			try {
				Thread.sleep((1000 * delay) + rInt);
			} catch (InterruptedException e) {
			}

			try {

				L.debug("start file download");
				URL url = new URL(spec); // you can write here any link

				Uri uri = Uri.parse(spec);
				String fileName = uri.getLastPathSegment();
				File fileDir = new File(dir);

				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}

				File file = new File(fileDir, fileName);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				try {
					InputStream is = conn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					/*
					 * Read bytes to the Buffer until there is nothing more to
					 * read(-1).
					 */
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}

					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baf.toByteArray());
					fos.close();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}

				L.debug("file download complete");
				return file.getAbsolutePath();

			} catch (IOException e) {
				L.error("" + e);
			}

		}

		return null;

	}

	private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}

}
