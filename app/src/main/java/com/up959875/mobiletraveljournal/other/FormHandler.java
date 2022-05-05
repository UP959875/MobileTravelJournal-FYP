package com.up959875.mobiletraveljournal.other;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

//Handler to ensure that input boxes and forms are valid (e.g. not empty and valid email) and return a message if not.
public class FormHandler {

    public FormHandler(){

    }

    //Watcher on the input boxes.
    public void addWatcher(final TextInputEditText input, final TextInputLayout layout) {
        input.addTextChangedListener(new TextInputWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (input.hasFocus())
                    validateInput(input, layout);
            }
        });
    }


    /**
     * It takes in a TextInputEditText and a TextInputLayout and returns a boolean. If the
     * TextInputEditText is empty, it sets the error message of the TextInputLayout to "Field cannot be
     * empty" and returns false. If the TextInputEditText is not empty but the input type is email and
     * the email is invalid, it sets the error message of the TextInputLayout to "Invalid email" and
     * returns false. If the TextInputEditText is not empty and the input type is not email, it sets
     * the error message of the TextInputLayout to null and returns true
     * 
     * @param input The TextInputEditText that you want to validate
     * @param layout The TextInputLayout that wraps the TextInputEditText
     * @return A boolean value.
     */
    public boolean validateInput(TextInputEditText input, TextInputLayout layout) {
        String value = input.getText() == null ? "" : input.getText().toString();
        layout.setErrorEnabled(true);
        if (value.isEmpty()) {
            layout.setError("Field cannot be empty");
            input.requestFocus();
            return false;
        } else if (input.getInputType() == (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + InputType.TYPE_CLASS_TEXT) && !isValidEmail(value)) {
            layout.setError("Invalid email");
            input.requestFocus();
            return false;
        } else
            layout.setErrorEnabled(false);
        return true;
    }


    /**
     * It checks if the two inputs are equal, if not, it sets the error message to the second input's
     * layout and requests focus on the second input
     * 
     * @param input1 The first input field
     * @param input2 The second input field
     * @param layout2 The layout of the second input
     * @return A boolean value.
     */
    public boolean validateInputsEquality(TextInputEditText input1, TextInputEditText input2, TextInputLayout layout2) {
        if (!isEqual(input1, input2)) {
            if(input1.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT) {
                layout2.setError("Password does not match");
                input2.requestFocus();
            }
            else if (input1.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + InputType.TYPE_CLASS_TEXT) {
                layout2.setError("Email does not match");
                input2.requestFocus();
            }
            return false;
        } else
            layout2.setErrorEnabled(false);
        return true;
    }

    private boolean isEqual(TextInputEditText input1, TextInputEditText input2) {
        if (input1.getText() != null && input2.getText() != null)
            return input1.getText().toString().equals(input2.getText().toString());
        else return false;
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void clearInput(TextInputEditText input, TextInputLayout layout) {
        clearText(input);
        offWatcher(layout);
        clearFocus(input);
    }


    private void clearText(TextInputEditText input) {
        input.setText("");
    }


    private void offWatcher(TextInputLayout layout) {
        layout.setErrorEnabled(false);
    }


    private void clearFocus(TextInputEditText input) {
        input.clearFocus();
    }
}
