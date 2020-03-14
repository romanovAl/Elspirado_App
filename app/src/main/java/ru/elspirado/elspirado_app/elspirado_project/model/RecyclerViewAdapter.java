package ru.elspirado.elspirado_app.elspirado_project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.libRG.CustomTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Recorder>
            arrayListAllData, //Хранит в себе все данные для ресайкла
            selectedArrayList; //Хранит выбранные для взаимодействия элементы

    private boolean[]
            isSelectedArray, //Хранит данные о элементах, выбранных для взаимодействия
            isOpenedFullArray; //Хранит данные о раскрытах полностью элементах

    private int countSelected;

    private Context context;

    public RecyclerViewAdapter(ArrayList<Recorder> arrayList, Context context){
        this.arrayListAllData = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recycler_view_item, parent, false);

//        ((ViewGroup)view.findViewById(R.id.view)).getLayoutTransition()
//                .enableTransitionType(LayoutTransition.CHANGING);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        //Получаем текущий объект
        final Recorder recorder = arrayListAllData.get(position);

        //Установка дефолтных значений

        ((ViewHolder) holder).customTextViewRecorderValue.setText(String.valueOf(recorder.getValue()));

        ((ViewHolder) holder).textViewNote.setVisibility(View.GONE);

        ((ViewHolder) holder).imageViewDeleteRecorder.setVisibility(View.GONE);

        ((ViewHolder) holder).imageViewEditRecorder.setVisibility(View.GONE);

        ((ViewHolder) holder).imageViewDropDown.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));

        ((ViewHolder) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));

        //Установка значений, изменённых ранее, сохранённых в массивах

        setCallback();

        if(isSelectedArray[position]){
            setCallback();
            ((ViewHolder) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.primaryLightColor));
        }

        if(isOpenedFullArray[position]){
            ((ViewHolder) holder).textViewNote.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).textViewNote.setText(recorder.getNote());

            ((ViewHolder) holder).imageViewDeleteRecorder.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).imageViewEditRecorder.setVisibility(View.VISIBLE);

            ((ViewHolder) holder).imageViewDropDown.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
        }

        //Работа с отображением стрелки роста/убывания грфаика

        try {
            Recorder lastRecorder = arrayListAllData.get(position - 1);

            ((ViewHolder) holder).imageViewTrending.setVisibility(View.VISIBLE);

            if(lastRecorder.getValue() < recorder.getValue()){
                ((ViewHolder) holder).imageViewTrending.setImageDrawable(context.getDrawable(R.drawable.ic_trending_up_black_36dp));
            } else

            if(lastRecorder.getValue() > recorder.getValue()){
                ((ViewHolder) holder).imageViewTrending.setImageDrawable(context.getDrawable(R.drawable.ic_trending_down_black_36dp));
            } else

            if(lastRecorder.getValue() == recorder.getValue()){
                ((ViewHolder) holder).imageViewTrending.setImageDrawable(context.getDrawable(R.drawable.ic_trending_flat_black_36dp));
            }
        } catch (IndexOutOfBoundsException e){
            ((ViewHolder) holder).imageViewTrending.setVisibility(View.INVISIBLE);
        }

        //Работа с датой и временем

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ", Locale.getDefault());

        Date date = new Date();
        date.setTime(recorder.getTime());

        Calendar calendarRecorderTime = Calendar.getInstance();
        calendarRecorderTime.setTime(date);

        Calendar calendarRecorderDate = Calendar.getInstance();
        calendarRecorderDate.setTime(date);

        String timeString = timeFormat.format(calendarRecorderTime.getTime());

        String dateString = dateFormat.format(calendarRecorderDate.getTime());

        ((ViewHolder) holder).textViewTime.setText(timeString);

        ((ViewHolder) holder).textViewDate.setText(dateString);

        //Работаем с полем "До, или после лекарства"

        String isMedicine;

        if (recorder.getIsMedicine() == 0) {
            isMedicine = Objects.requireNonNull(context).getString(R.string.without_medicine);
        } else {
            isMedicine = Objects.requireNonNull(context).getString(R.string.after_medicine);
        }

        ((ViewHolder) holder).textViewIsMedicine.setText(isMedicine);

        //Обработка долгих нажатий на элемент

        ((ViewHolder) holder).constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(countSelected == 0){

                    isSelectedArray[position] = true;
                    countSelected++;

                    ConstraintLayout mainLay = ((ViewHolder) holder).constraintLayout;
                    mainLay.setBackgroundColor(context.getResources().getColor(R.color.primaryLightColor));

                    selectedArrayList.add(recorder);

                    setCallback();
                }

                return true;
            }
        });

        //Обработка быстрых нажатий на элемент

        ((ViewHolder) holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countSelected > 0){

                    if(isSelectedArray[position]){

                        isSelectedArray[position] = false;
                        countSelected--;

                        ((ViewHolder) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));

                        selectedArrayList.remove(recorder);

                        setCallback();

                    } else {

                        isSelectedArray[position] = true;
                        countSelected++;

                        ((ViewHolder) holder).constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.primaryLightColor));

                        selectedArrayList.add(recorder);

                        setCallback();
                    }

                } else {

                    if(isOpenedFullArray[position]){

                        isOpenedFullArray[position] = false;

                        ((ViewHolder) holder).textViewNote.setVisibility(View.GONE);

                        ((ViewHolder) holder).imageViewDeleteRecorder.setVisibility(View.GONE);
                        ((ViewHolder) holder).imageViewEditRecorder.setVisibility(View.GONE);

                        ((ViewHolder) holder).imageViewDropDown.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));

                    } else {

                        isOpenedFullArray[position] = true;

                        ((ViewHolder) holder).textViewNote.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).textViewNote.setText(recorder.getNote());

                        ((ViewHolder) holder).imageViewDeleteRecorder.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).imageViewEditRecorder.setVisibility(View.VISIBLE);

                        ((ViewHolder) holder).imageViewDropDown.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
                    }
                }
            }
        });

        //Обработка нажатий на кнопки

        ((ViewHolder) holder).imageViewEditRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.recyclerViewAdapterEditElementListener(recorder);

            }
        });

        ((ViewHolder) holder).imageViewDeleteRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.recyclerViewAdapterDeleteOneElementListener(recorder);
            }
        });
    }

    //Передаёт в MainActivity arrayList с объектами, которые нужно удалить
    public void deleteElements(){
        mListener.recyclerViewAdapterDeleteListener(selectedArrayList);
    }

    public void shareElements(){
        mListener.recyclerViewAdapterShareListener(selectedArrayList);
    }

    //Снимает выделение по лонгтапу, снимает раскрытые полностью элементы
    public void cleanSelection(){

        isSelectedArray = new boolean[arrayListAllData.size()];
        isOpenedFullArray = new boolean[arrayListAllData.size()];
        countSelected = 0;

        selectedArrayList.clear();

        super.notifyDataSetChanged();
    }

    public void selectAllElements(){

        for(int i = 0; i < arrayListAllData.size(); i++){
            isSelectedArray[i] = true;
        }

        countSelected = arrayListAllData.size();
        selectedArrayList.clear();
        selectedArrayList.addAll(arrayListAllData);

        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(arrayListAllData == null)
            return 0;
        else
            return arrayListAllData.size();
    }

    //Getters and setters

    public ArrayList<Recorder> getSelectedArrayList() {
        return selectedArrayList;
    }

    public void setSelectedArrayList(ArrayList<Recorder> selectedArrayList) {
        this.selectedArrayList = selectedArrayList;
    }

    public boolean[] getIsOpenedFullArray() {
        return isOpenedFullArray;
    }

    public void setIsOpenedFullArray(boolean[] isOpenedFullArray) {
        this.isOpenedFullArray = isOpenedFullArray;
    }

    public boolean[] getIsSelectedArray() {
        return isSelectedArray;
    }

    public void setIsSelectedArray(boolean[] isSelectedArray) {
        this.isSelectedArray = isSelectedArray;
    }

    public int getCountSelected() {
        return countSelected;
    }

    public void setCountSelected(int countSelected) {
        this.countSelected = countSelected;
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout constraintLayout;

        TextView textViewTime, textViewIsMedicine, textViewDate;

        CustomTextView customTextViewRecorderValue;

        ImageView imageViewTrending, imageViewDropDown;

        ImageView imageViewEditRecorder, imageViewDeleteRecorder;

        TextView textViewNote;

        View viewBottomLine;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.view);

            textViewTime = itemView.findViewById(R.id.textView_recorder_time);
            textViewIsMedicine = itemView.findViewById(R.id.textView_recorder_is_medicine);

            textViewDate = itemView.findViewById(R.id.textView_recorder_date);

            customTextViewRecorderValue = itemView.findViewById(R.id.customTextView_recorder_value);

            imageViewTrending = itemView.findViewById(R.id.imageView_trending);
            imageViewDropDown = itemView.findViewById(R.id.imageView_drop_down);

            imageViewEditRecorder = itemView.findViewById(R.id.btn_edit);
            imageViewDeleteRecorder = itemView.findViewById(R.id.btn_delete);

            viewBottomLine = itemView.findViewById(R.id.view_bottom_line);

            textViewNote = itemView.findViewById(R.id.textView_note);
        }
    }

    private void setCallback(){

        mListener.recyclerViewAdapterSelectedElementsListener(selectedArrayList.size());
    }

    public interface OnFragmentListener{

        void recyclerViewAdapterSelectedElementsListener(int size);

        void recyclerViewAdapterDeleteListener(ArrayList<Recorder> arrayList);

        void recyclerViewAdapterShareListener(ArrayList<Recorder> arrayList);

        void recyclerViewAdapterDeleteOneElementListener(Recorder recorder);

        void recyclerViewAdapterEditElementListener(Recorder recorder);
    }

    private OnFragmentListener mListener;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if(context instanceof OnFragmentListener){
            mListener = (OnFragmentListener) context;
        } else{
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentListener");
        }
    }
}