package ru.elspirado.elspirado_app.elspirado_project.model;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.elspirado.elspirado_app.elspirado_project.model.roomStuff.RecorderDao;
import ru.elspirado.elspirado_app.elspirado_project.model.roomStuff.RecorderRoomDataBase;

public class DBRecorder {

    private FirebaseUser currentUser;

    private RecorderDao recorderDao;

    public DBRecorder() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void createRecorderDao() {
        RecorderRoomDataBase db = App.getInstance().getDatabase();
        recorderDao = db.recorderDao();
    }

    public interface CallbackSelectAllListener {
        void DBRecorderCallingBackSelectAll(ArrayList<Recorder> arrayList);
    }

    public interface CallbackSortedArrayListener {
        void DBRecorderCallingBackSortedArray(ArrayList<Recorder> arrayList);
    }

    public interface CallbackDeleteListener{
        void DBRecorderCallingBackDelete();
    }

    public interface CallbackDeleteArrayListListener{
        void DBRecorderCallingBackDeleteArrayList();
    }

    public interface CallbackInsertListener{
        void DBRecorderCallingBackInsert();
    }

    public interface CallbackUpdateListener{
        void DBRecorderCallingBackUpdate();
    }

    public interface CallbackDeleteAllListener{
        void DBRecorderCallingBackDeleteAll();
    }

    public void insert(int value, String note, long time, int isMedicine,  final CallbackInsertListener callbackInsertListener) {

        Date date = new Date();

        long id = date.getTime();

        Recorder recorder = new Recorder(String.valueOf(id), value, note, time, isMedicine);

        if (currentUser == null) {
            insertWithRoom(value, note, time, isMedicine)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            callbackInsertListener.DBRecorderCallingBackInsert();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {
            Utils.getDatabase()
                    .getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("records")
                    .child(String.valueOf(id)).setValue(recorder);

            callbackInsertListener.DBRecorderCallingBackInsert();
        }
    }

    private Completable insertWithRoom(int value, String note, long time, int isMedicine){
        if(recorderDao == null){
            createRecorderDao();
        }

        Date date = new Date();

        long id = date.getTime();

        final Recorder recorder = new Recorder(String.valueOf(id), value, note, time, isMedicine);

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                recorderDao.insert(recorder);
                emitter.onComplete();
            }
        });
    }

    public void insert(ArrayList<Recorder> arrayList) { //TODO этот метод используется 1,5 раза, и он странный

        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setId(String.valueOf(i));
        }

        Utils.getDatabase()
                .getReference()
                .child("users")
                .child(currentUser.getUid())
                .child("records")
                .setValue(arrayList);
    }

    public void update(Recorder recorder, final DBRecorder.CallbackUpdateListener callbackUpdateListener) {

        if (currentUser == null) {
            updateWithRoom(recorder)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            callbackUpdateListener.DBRecorderCallingBackUpdate();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {
            Utils.getDatabase()
                    .getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("records")
                    .child(String.valueOf(recorder.getId()))
                    .setValue(recorder);

            callbackUpdateListener.DBRecorderCallingBackUpdate();
        }
    }

    private Completable updateWithRoom(final Recorder recorder){
        if(recorderDao == null){
            createRecorderDao();
        }

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                recorderDao.update(recorder);
                emitter.onComplete();
            }
        });
    }

    public void delete(String id, final CallbackDeleteListener callbackDeleteListener) {

        if (currentUser == null) {
            deleteWithRoom(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            callbackDeleteListener.DBRecorderCallingBackDelete();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {
            Utils.getDatabase()
                    .getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("records")
                    .child(String.valueOf(id))
                    .removeValue();

            callbackDeleteListener.DBRecorderCallingBackDelete();
        }
    }

    private Completable deleteWithRoom(final String id){
        if(recorderDao == null){
            createRecorderDao();
        }
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                recorderDao.delete(recorderDao.select(id));
                emitter.onComplete();
            }
        });
    }

    public void deleteAllWithFirebase(){

        Utils.getDatabase()
                .getReference()
                .child("users")
                .child(currentUser.getUid())
                .child("records").removeValue();
    }

    public void deleteAllWithRoom(final CallbackDeleteAllListener callbackDeleteAllListener) {
        completableDeleteAllWithRoom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        callbackDeleteAllListener.DBRecorderCallingBackDeleteAll();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private Completable completableDeleteAllWithRoom(){
        if(recorderDao == null){
            createRecorderDao();
        }

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                recorderDao.deleteAll();
                emitter.onComplete();
            }
        });
    }

    public void deleteArrayList(ArrayList<Recorder> arrayList, final CallbackDeleteArrayListListener callbackDeleteArrayListListener){

        if(currentUser == null){
            completableDeleteArrayListWithRoom(arrayList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            callbackDeleteArrayListListener.DBRecorderCallingBackDeleteArrayList();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {

            for (int i = 0; i < arrayList.size(); i++){
                Utils.getDatabase()
                        .getReference()
                        .child("users")
                        .child(currentUser.getUid())
                        .child("records")
                        .child(String.valueOf(arrayList.get(i).getId()))
                        .removeValue();
            }

            callbackDeleteArrayListListener.DBRecorderCallingBackDeleteArrayList();

        }
    }

    private Completable completableDeleteArrayListWithRoom(final ArrayList<Recorder> arrayList){
        if(recorderDao == null){
            createRecorderDao();
        }

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {

                for (int i = 0; i < arrayList.size(); i++){
                    recorderDao.delete(arrayList.get(i));
                }

                emitter.onComplete();
            }
        });
    }

    private ArrayList<Recorder> arr = new ArrayList<>();

    public void selectAll(final CallbackSelectAllListener callbackSelectAllListener) {

        if (currentUser == null) {
            selectAllWithRoom()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<ArrayList<Recorder>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(ArrayList<Recorder> arrayList) {
                            callbackSelectAllListener.DBRecorderCallingBackSelectAll(arrayList);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {
            DatabaseReference ref = Utils.getDatabase().getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("records");

            ref.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
                @TargetApi(VERSION_CODES.KITKAT)
                @RequiresApi(api = VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        arr.add(dsp.getValue(Recorder.class));
                    }

                    callbackSelectAllListener.DBRecorderCallingBackSelectAll(arr);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public Single<ArrayList<Recorder>> selectAllWithRoom(){

        if (recorderDao == null) {
            createRecorderDao();
        }

        return Single.create(new SingleOnSubscribe<ArrayList<Recorder>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<Recorder>> emitter){

                List<Recorder> list = recorderDao.selectAll();

                ArrayList<Recorder> arrayList = new ArrayList<>(list);

                emitter.onSuccess(arrayList);
            }
        });
    }

    private ArrayList<Recorder> sortedByTimeArr = new ArrayList<>();

    public void getTimeRange(final long fromTime, final long toTime, final CallbackSortedArrayListener callbackSortedArrayListener) {

        if (currentUser == null) {

            getTimeRangeWithRoom(fromTime, toTime)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<ArrayList<Recorder>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(ArrayList<Recorder> recorders) {
                            callbackSortedArrayListener.DBRecorderCallingBackSortedArray(recorders);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });


        } else {
            Query mySortedQuery = Utils.getDatabase().getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("records")
                    .orderByChild("time")
                    .startAt(fromTime)
                    .endAt(toTime);

            mySortedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        sortedByTimeArr.add(dsp.getValue(Recorder.class));
                    }

                    Collections.sort(sortedByTimeArr);
                    callbackSortedArrayListener.DBRecorderCallingBackSortedArray(sortedByTimeArr);
                    sortedByTimeArr = new ArrayList<>();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private Single<ArrayList<Recorder>> getTimeRangeWithRoom(final long fromTime, final long toTime) {
        return Single.create(new SingleOnSubscribe<ArrayList<Recorder>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<Recorder>> emitter) {
                if (recorderDao == null) {
                    createRecorderDao();
                }

                List<Recorder> list = recorderDao.selectTimeRange(fromTime, toTime);
                final ArrayList<Recorder> recorderArrayList = new ArrayList<>(list);
                emitter.onSuccess(recorderArrayList);
            }
        });
    }
}