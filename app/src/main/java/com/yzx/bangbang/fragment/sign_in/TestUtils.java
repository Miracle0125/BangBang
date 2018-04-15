package com.yzx.bangbang.fragment.sign_in;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.SignIn;
import com.yzx.bangbang.model.Contact;
import com.yzx.bangbang.utils.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestUtils {

    private static final String[] test_accounts = {
            "Eddard “Ned” Stark ", "Robert Baratheon ", "Tyrion Lannister ", "Cersei Lannister ", "Jaime Lannister ", "Daenerys Targaryen ", "Jon Snow ", "Sansa Stark ", "Bran Stark ", "Petyr Baelish ", "Sandor Clegane"
    };

    public static AlertDialog show_test_account(SignIn signIn, View.OnClickListener onClickListener) {

        View v = signIn.getLayoutInflater().inflate(R.layout.scroll_layout0, null);
        LinearLayout content = v.findViewById(R.id.content);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, util.dp2px(50));
        lp.setMarginStart(util.dp2px(30));

        for (int i = 0; i < test_accounts.length; i++) {
            TextView t = new TextView(signIn);
            t.setLayoutParams(lp);
            t.setText(test_accounts[i]);
            t.setClickable(true);
            t.setOnClickListener(onClickListener);
            t.setTag("t" + i);
            content.addView(t);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(signIn);
        builder.setMessage("test account");
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private static final String[] test_names = new String[]{
            "Eddard “Ned” Stark",
            "Robert Baratheon",
            "Tyrion Lannister",
            "Cersei Lannister",
            "Jaime Lannister",
            "Daenerys Targaryen",
            "Jon Snow",
            "Sansa Stark",
            "Bran Stark"
    };
    private static final int[] test_first_letter = {
            4,
            17,
            19,
            2,
            9,
            3,
            9,
            18,
            1,
            15};


    public static List<Contact> get_test_contacts() {
        List<Contact> test_contacts = new ArrayList<>();
        for (int i = 0; i < test_names.length; i++) {
            Contact contact = new Contact();
            contact.person_id = 117 + i;
            contact.person_name = test_names[i];
            contact.first_letter = test_first_letter[i];
            test_contacts.add(contact);
        }
        Collections.sort(test_contacts, (a, b) -> {
            if (a.first_letter == b.first_letter) return 0;
            return a.first_letter < b.first_letter ? -1 : 1;
        });
        //test_contacts.sort((Comparator<Contact>) (a, b) -> a.first_letter < b.first_letter);
        return test_contacts;
    }
}
