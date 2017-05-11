package menuFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import animationStuff.FloatingActionAnimation;
import it.pyronaid.brainstorming.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCategoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCategoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean visible = false;
    private List<FloatingActionAnimation> sub_animations = new ArrayList<FloatingActionAnimation>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyCategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCategoriesFragment newInstance(String param1, String param2) {
        MyCategoriesFragment fragment = new MyCategoriesFragment();
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
        View rootview = inflater.inflate(R.layout.fragment_my_categories, container, false);
        FloatingActionButton f = (FloatingActionButton) rootview.findViewById(R.id.fab_my_categories);
        FloatingActionButton f1 = (FloatingActionButton) rootview.findViewById(R.id.fab_first);
        FloatingActionButton f2 = (FloatingActionButton) rootview.findViewById(R.id.fab_second);
        FloatingActionButton f3 = (FloatingActionButton) rootview.findViewById(R.id.fab_third);
        sub_animations.add(new FloatingActionAnimation(
                f1,
                AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab1_show),
                AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab1_hide),
                1.7,0.25));
        sub_animations.add(new FloatingActionAnimation(
                f2,
                AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab2_show),
                AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab2_hide),
                1.15,1.4));
        sub_animations.add(new FloatingActionAnimation(
                f3,
                AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab3_show),
                AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab3_hide),
                0,1.65));


        //Animations


        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perform_action_fab(visible);
            }
        });
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "MA VAIIIII!!", Toast.LENGTH_SHORT).show();
            }
        });
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Isometria!!", Toast.LENGTH_SHORT).show();
            }
        });
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bisogna farne di più e meglio!!", Toast.LENGTH_SHORT).show();
            }
        });


        return rootview;
    }

    private void perform_action_fab(boolean visible) {
        if(visible){
            for(FloatingActionAnimation sub_animation : sub_animations) {
                FloatingActionButton sub_button = sub_animation.getFloatingActionButton();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sub_button.getLayoutParams();
                layoutParams.rightMargin -= (int) (sub_button.getWidth() * sub_animation.getWidth());
                layoutParams.bottomMargin -= (int) (sub_button.getHeight() * sub_animation.getHeight());
                sub_button.setLayoutParams(layoutParams);
                sub_button.startAnimation(sub_animation.getAnimation_hide());
                sub_button.setClickable(false);
            }
        } else {
            for(FloatingActionAnimation sub_animation : sub_animations){
                FloatingActionButton sub_button = sub_animation.getFloatingActionButton();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sub_button.getLayoutParams();
                layoutParams.rightMargin += (int) (sub_button.getWidth() * sub_animation.getWidth());
                layoutParams.bottomMargin += (int) (sub_button.getHeight() * sub_animation.getHeight());
                sub_button.setLayoutParams(layoutParams);
                sub_button.startAnimation(sub_animation.getAnimation_show());
                sub_button.setClickable(true);
            }
        }
        this.visible = !visible;
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
}
