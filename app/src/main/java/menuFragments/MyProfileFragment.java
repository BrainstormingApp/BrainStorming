package menuFragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import adapter.AccountInformationAdapter;
import adapter.CustomLinearLayoutManager;
import asynctask.ProfileImageProcedureTask;
import cropperStuff.CropImage;
import cropperStuff.CropImageView;
import intentStuff.RequestCodeGeneral;
import it.pyronaid.brainstorming.MainActivity;
import it.pyronaid.brainstorming.R;
import layoutCustomized.DynamicImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private DynamicImageView dynamicImageView;
    private AccountInformationAdapter accountInformationAdapter;
    private Cursor cursor;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        cursor = ((MainActivity) getActivity()).getBrainStormingSQLiteHelper().getUserInfo();
        accountInformationAdapter = new AccountInformationAdapter(this, cursor);

        dynamicImageView = (DynamicImageView) rootView.findViewById(R.id.big_profile_picture);
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        String directory = cw.getDir("profile", Context.MODE_PRIVATE).getAbsolutePath();
        File file = new File(directory+"/Profile.jpg");
        if(file.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap finalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            dynamicImageView.setImageBitmap(finalBitmap);
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.user_information);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        CustomLinearLayoutManager mLayoutManager = new CustomLinearLayoutManager(getActivity());
        mLayoutManager.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(accountInformationAdapter);

        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.button_edit_picture);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCropImageActivity(null);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCodeGeneral.REQ_EDIT && resultCode == Activity.RESULT_OK) {
            cursor = ((MainActivity) getActivity()).getBrainStormingSQLiteHelper().getUserInfo();
            accountInformationAdapter.changeCursor(cursor);
            //Toast.makeText(getActivity(), "Change Done!!", Toast.LENGTH_SHORT).show();
            //some code
        } else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                //Make different staff
                new ProfileImageProcedureTask(this, dynamicImageView).execute(result.getUri());
                Toast.makeText(getActivity(), "Cropping successful", Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }

        }

    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowCounterRotation(true)
                .start(getContext(), this);
    }
}
