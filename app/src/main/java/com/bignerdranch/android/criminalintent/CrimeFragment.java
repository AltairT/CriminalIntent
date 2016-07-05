package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ting on 2016/6/26.
 */
public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mDeleteCrime;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mDialButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    private String id;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        UUID crimeId = (UUID) getActivity().getIntent()
//                .getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
//        mCrime = new Crime();
        //setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    //这个是CrimeListFragment的菜单，放这里只是测试
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDeleteCrime = (Button) v.findViewById(R.id.del_crime);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mDialButton = (Button) v.findViewById(R.id.crime_dial);
        //mDateButton.setText(mCrime.getDate().toString());
        updateDate();
        //=    mDateButton.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mCrime.getDate()));

//        mDateButton.setEnabled(false);
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //测试禁用联系人按钮
        // pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
            mSuspectButton.setTextColor(Color.RED);
        }
        mDialButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCrime.getPhoneNum() == null) {
                    Toast.makeText(getActivity(), "还没点选联系人", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri number = Uri.parse("tel:" + mCrime.getPhoneNum());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(number);
                startActivity(intent);

            }
        });
        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mDeleteCrime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CrimeLab.get(getActivity()).delCrime(mCrime);
                Toast.makeText(getActivity(), "删除完成", Toast.LENGTH_SHORT).show();
                //
                getActivity().finish();
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);

            }
        });
        //检查有没有联系人应用
        //final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
            mSuspectButton.setText("没有联系人应用！");
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView(); //首次更新图片
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        boolean canTakePhoto = (mPhotoFile != null
                && captureImage.resolveActivity(packageManager) != null);
        //  boolean canTakePhoto = (mPhotoFile != null
        //          && packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY) != null);


        mPhotoButton.setEnabled(canTakePhoto);


        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);

            }
        });
        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFieldsName = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            String[] queryFieldsPhoneID = new String[]{
                    ContactsContract.Contacts._ID
            };
            String[] queryFieldsPhoneNum = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFieldsName, null, null, null);
            try {
                if (c.getCount() == 0) {
                    mSuspectButton.setTextColor(Color.BLACK);
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                mSuspectButton.setTextColor(Color.RED);
            } finally {
                c.close();

            }

            // Cursor   查ID
            c = getActivity().getContentResolver()
                    .query(contactUri, queryFieldsPhoneID, null, null, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                //String
                id = c.getString(0);
                //Toast.makeText(getActivity(), "查到的ID是 " + id,Toast.LENGTH_SHORT).show();
                //mCrime.setSuspect(suspect);
                //mSuspectButton.setText(suspect);
                // mSuspectButton.setTextColor(Color.RED);
            } finally {
                c.close();

            }

            // Cursor   查电话号码
            c = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id}, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }
                /*可以使用以下方式读取全部的联系方式
                 while(phoneCursor.moveToNext()) {
                 phoneNumber = phoneCursor.getString(
                phoneCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                System.out.println("id="+id+" name="+name+" phoneNumber="+phoneNumber);
                 }


                 */
                c.moveToFirst();
                String phoneNum = c.getString(c.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                Toast.makeText(getActivity(), "查到的ID " + id + " Num " + phoneNum, Toast.LENGTH_SHORT).show();
                mCrime.setPhoneNum(phoneNum);
                //mCrime.setSuspect(suspect);
                //mSuspectButton.setText(suspect);
                // mSuspectButton.setTextColor(Color.RED);
            } finally {
                c.close();

            }
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView(); //拍照返回后更新图片
        }
    }

    private void updateDate() {
        mDateButton.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        String crimeTitle = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        if (mCrime.getTitle() == null) {
            crimeTitle = "为空";
        } else {
            crimeTitle = mCrime.getTitle();
        }
        String dateFormat = "MMMd日 EEE";
//        String dateString = new DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String dateString = new SimpleDateFormat(dateFormat).format(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report =
                getString(R.string.crime_report, crimeTitle, dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
