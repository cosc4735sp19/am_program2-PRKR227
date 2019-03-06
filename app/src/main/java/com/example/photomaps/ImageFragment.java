package com.example.photomaps;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ImageFragment extends DialogFragment {

    String TAG = "Image_Fragment";
    Context myContext;
    public ImageView mImageView;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(Bitmap pic) {
        ImageFragment fragment = new ImageFragment();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bundle args = new Bundle();
        args.putByteArray("image", byteArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        byte[] byteArray = getArguments().getByteArray("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mImageView = view.findViewById(R.id.imageView);
        mImageView.setImageBitmap(bmp);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = context;
        Log.d(TAG, "onAttach");
    }
}

