package com.demo.imagefilter.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by michael on 14-1-21.
 */
public class utils {

    public static final void saveBitmapImage(Context context, Bitmap bt, String name) {
        new Thread(new SaveRunnable(context, bt, name)).start();
    }

    public static final class SaveRunnable implements Runnable {
        Bitmap mBitmap;

        Context context;

        String filename;

        public SaveRunnable(Context context, Bitmap bitmap, String filename) {
            mBitmap = bitmap;
            this.context = context;
            this.filename = filename;
        }

        @Override
        public void run() {
            if (mBitmap == null) {
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
//            String pictureName = String.format("filter_%d%02d%02d_%02d%02d%02d",
//                                                  calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)
//                                                                                   + (1 - Calendar.JANUARY),
//                                                  calendar.get(Calendar.DATE),
//                                                  calendar.get(Calendar.HOUR_OF_DAY),
//                                                  calendar.get(Calendar.MINUTE),
//                                                  calendar.get(Calendar.SECOND));
            String pictureName = filename;

            File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            filePath = new File(filePath, "/filter/");
            filePath.mkdirs();
            filePath = new File(filePath, pictureName + ".jpeg");

            try {
                filePath.createNewFile();
                FileOutputStream fos = new FileOutputStream(filePath);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                ExifInterface exif = new ExifInterface(
                                                          filePath.getAbsolutePath());
                exif.setAttribute(ExifInterface.TAG_ORIENTATION,
                                     Integer.toString(ExifInterface.ORIENTATION_NORMAL));
                exif.saveAttributes();

                ContentValues v = new ContentValues();
                v.put(MediaStore.MediaColumns.TITLE, pictureName);
                v.put(MediaStore.MediaColumns.DISPLAY_NAME, pictureName);
                v.put(MediaStore.Images.ImageColumns.DESCRIPTION, "filter image");
                v.put(MediaStore.MediaColumns.DATE_ADDED, calendar.getTimeInMillis());
                v.put(MediaStore.Images.ImageColumns.DATE_TAKEN, calendar.getTimeInMillis());
                v.put(MediaStore.MediaColumns.DATE_MODIFIED, calendar.getTimeInMillis());
                v.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                v.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
                v.put(MediaStore.MediaColumns.DATA, filePath.getAbsolutePath());

                File parent = filePath.getParentFile();
                String path = parent.toString().toLowerCase(Locale.ENGLISH);
                String name = parent.getName().toLowerCase(Locale.ENGLISH);
                v.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
                v.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
                v.put(MediaStore.MediaColumns.SIZE, filePath.length());

                ContentResolver c = context.getContentResolver();
                c.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

}
