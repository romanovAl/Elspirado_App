package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {

    private NewFragmentGraph fragmentGraph;
    private FragmentRecyclerView fragmentRecyclerView;
    private FragmentUser fragmentUser;

    public FragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {

        super(fm, behavior);

        fragmentGraph = new NewFragmentGraph();
        fragmentRecyclerView = new FragmentRecyclerView();
        fragmentUser = new FragmentUser();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 1:
                return fragmentRecyclerView;

            case 2:
                return fragmentUser;

                default:
                    return fragmentGraph;
        }

    }

    public void checkProvidersInFragmentUser(){
        fragmentUser.checkProviders();
    }

    public void cleanSelectionInFragmentRecyclerView(){
        fragmentRecyclerView.cleanSelectionInRecyclerView();
    }

    public void shareSelectedElementsInFragmentRecyclerView(){
        fragmentRecyclerView.shareElementsInRecyclerView();
    }

    public void selectAllElementsInFragmentRecyclerView(){
        fragmentRecyclerView.selectAllElementsInRecyclerView();
    }

    public void deleteSelectedElementsInFragmentRecyclerView(){
        fragmentRecyclerView.deleteElementInRecyclerView();
    }

    public int getSelectedItemCountInFragmentRecyclerView(){
        return fragmentRecyclerView.getRecyclerViewSelectedItemCount();
    }
    @Override
    public int getCount() {
        return 3;
    }
}
