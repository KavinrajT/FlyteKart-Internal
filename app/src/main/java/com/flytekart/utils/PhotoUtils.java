package com.flytekart.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoUtils {

    public static BitmapFactory.Options getDimens(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path) {

        BitmapFactory.Options options = getDimens(path);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, Constants.IMAGE_MAX_DIM, Constants.IMAGE_MAX_DIM);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static byte[] getByteArrayFromFile(File f) {
        InputStream is;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        byte[] byteArrayImage = new byte[(int) f.length()];
        try {
            is.read(byteArrayImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return byteArrayImage;
    }

    public static Bitmap getBitmapFromPath(Context context, String path) {
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = PhotoUtils.decodeSampledBitmapFromResource(path);
        ExifInterface ei = null;
        Bitmap imageBitmap = null;
        try {
            //InputStream in = context.getContentResolver().openInputStream(Uri.parse(path));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ei = new ExifInterface(path);
            } else {
                ei = new ExifInterface(path);
            }
            //ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
//                    imageBitmap = PhotoUtil.getInstance(context).rotateBitmap(bmp, 90);
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }
            imageBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }

    private static byte[] getByteArrayImage(Context context, String imagePath, float sizeRatio) {
        imagePath = Uri.parse(imagePath).getPath();
        byte[] byteArrayImage;
        Bitmap bmp;

        bmp = PhotoUtils.getBitmapFromPath(context, imagePath);

        if (bmp != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, (int) (sizeRatio * 100), bos);
            byteArrayImage = bos.toByteArray();

            try {
                bos.close();
                bmp.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (byteArrayImage != null) {
                int sizeInKb = byteArrayImage.length / 1024;

                if (sizeInKb > Constants.MAX_IMAGE_SIZE) {
                    sizeRatio = sizeRatio * 0.8f;
                    byteArrayImage = getByteArrayImage(context, imagePath, sizeRatio);
                }
            }
        } else {
            byteArrayImage = PhotoUtils.getByteArrayFromFile(new File(imagePath));
            //int sizeInKb = byteArrayImage.length / 1024;
            //Logger.e("File size 2: " + sizeInKb);
        }
        int sizeInKb = byteArrayImage.length / 1024;
        Logger.e("File sizeeee: " + sizeInKb);
        return byteArrayImage;
    }

    public static File compressImage(Context mContext, String imagePath, float sizeRatio) {
        imagePath = Uri.parse(imagePath).getPath();
        byte[] byteArrayImage = getByteArrayImage(mContext, imagePath, sizeRatio);
        String fileName = System.currentTimeMillis() + Constants.STR_JPEG_FILE_EXTENSION;
        File file = new File(mContext.getFilesDir(), fileName);

        if (byteArrayImage != null) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(byteArrayImage);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
