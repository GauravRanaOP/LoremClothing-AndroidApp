package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class CheckoutActivity extends AppCompatActivity {

    EditText postalCodeEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText cardNumberEditText;
    EditText cardName;
    LinearLayout expiryDateCvvLayout;
    EditText expiryDateEditText;
    EditText cvvEditText;

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText addressEditText;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference database;
    StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance().getReference("products");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        ImageView cartlogout = findViewById(R.id.logout);

        cartlogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        postalCodeEditText = findViewById(R.id.postalCodeCheckout);
        emailEditText = findViewById(R.id.emailCheckout);
        phoneEditText = findViewById(R.id.phoneCheckout);
        cardNumberEditText = findViewById(R.id.cardNumberCheckout);
        cardName = findViewById(R.id.cardNameCheckout);
        expiryDateCvvLayout = findViewById(R.id.expiryDateCvvLayout);
        expiryDateEditText = findViewById(R.id.expiryDateCheckout);
        cvvEditText = findViewById(R.id.cvvCheckout);

        firstNameEditText = findViewById(R.id.firstNameCheckout);
        lastNameEditText = findViewById(R.id.lastNameCheckout);
        addressEditText = findViewById(R.id.addressCheckout);

        // Set up validation and listeners for EditText fields
        setupValidation();
        setupPaymentOptionsListener();
        setupSubmitButton();
    }

    private void setupValidation() {
        // Postal Code Validation
        postalCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 6) {
                    postalCodeEditText.setError("Postal code must be 6 characters");
                }else{
                    postalCodeEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Email Validation
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = emailEditText.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid email address");
                }else {
                    emailEditText.setError(null);
                }
            }
        });

        // Phone Number Validation
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 10) {
                    phoneEditText.setError("Phone number must be 10 digits");
                } else {
                    phoneEditText.setError(null);}
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Card Number Validation and Masking
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            private static final int TOTAL_LENGTH = 16;
            private static final int PARTIAL_MASK_LENGTH = 4;
            private boolean deleting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                deleting = count > after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 16) {
                    cardNumberEditText.setError("Card Number must be 16 digits");
                } else{
                    cardNumberEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                cardNumberEditText.removeTextChangedListener(this);
                String currentText = s.toString();
                if (currentText.length() < TOTAL_LENGTH) {
                    cardNumberEditText.addTextChangedListener(this);
                    return;
                }

                StringBuilder masked = new StringBuilder();
                for (int i = 0; i < TOTAL_LENGTH; i++) {
                    if (i < TOTAL_LENGTH - PARTIAL_MASK_LENGTH) {
                        masked.append('*');
                    } else {
                        masked.append(currentText.charAt(i));
                    }

                    if ((i + 1) % 4 == 0 && i < TOTAL_LENGTH - 1) {
                        masked.append(' ');
                    }
                }

                cardNumberEditText.setText(masked.toString());
                cardNumberEditText.setSelection(masked.length());
                cardNumberEditText.addTextChangedListener(this);
            }
        });

        // Expiry Date Validation and Masking
        expiryDateEditText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int prevLength;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                if (!isFormatting && length == 2 && prevLength < length) {
                    isFormatting = true;
                    expiryDateEditText.setText(s + "/");
                    expiryDateEditText.setSelection(expiryDateEditText.getText().length());
                    isFormatting = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isFormatting) {
                    String expiryDate = s.toString().replace("/", "");
                    Boolean expiry = isValidExpiryDate(expiryDate);
                    if(!expiry){
                        expiryDateEditText.setError("Expiry Date must be a future Date");
                    } else{
                        expiryDateEditText.setError(null);
                    }

                }
            }
        });

        // CVV Validation
        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 3) {
                    cvvEditText.setError("CVV must be 3 digits");
                } else{
                    cvvEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPaymentOptionsListener() {
        // Payment Options Click Listener
        Spinner paymentOptionsSpinner = findViewById(R.id.paymentOptions);
        String[] paymentOptions = {"Select Payment Option", "Credit Card", "Debit Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        paymentOptionsSpinner.setAdapter(adapter);
        paymentOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    cardNumberEditText.setVisibility(View.INVISIBLE);
                    cardName.setVisibility(View.INVISIBLE);
                    expiryDateCvvLayout.setVisibility(View.INVISIBLE);
                } else if (position == 1) { // Credit Card
                    cardNumberEditText.setVisibility(View.VISIBLE);
                    cardName.setVisibility(View.VISIBLE);
                    expiryDateCvvLayout.setVisibility(View.VISIBLE);
                } else if (position == 2) { // Debit Card
                    cardNumberEditText.setVisibility(View.VISIBLE);
                    cardName.setVisibility(View.VISIBLE);
                    expiryDateCvvLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cardNumberEditText.setVisibility(View.INVISIBLE);
                cardName.setVisibility(View.INVISIBLE);
                expiryDateCvvLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean hasError(){
        Boolean valid = true;
        if(postalCodeEditText.getError() != null){
            valid = false;
        }
        if(emailEditText.getError() != null){
            valid = false;
        }
        if(phoneEditText.getError() != null){
            valid = false;
        }
        if(cardNumberEditText.getError() != null){
            valid = false;
        }
        if(expiryDateEditText.getError() != null){
            valid = false;
        }
        if(cvvEditText.getError() != null){
            valid = false;
        }
        return valid;
    }

    private void setupSubmitButton() {
        Button submitButton = findViewById(R.id.checkoutSubmit);
        submitButton.setOnClickListener(v -> {
            if (isValid() && hasError()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot cloth : dataSnapshot.getChildren()) {
                            Clothes clothes = cloth.getValue(Clothes.class);
                            databaseReference.child(clothes.getName()).child("quantity").setValue(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });
                startActivity(new Intent(this, ThanksActivity.class));
                finish();
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private boolean isValid() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String postalCode = postalCodeEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String cardname = cardName.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();

        return !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) &&
                !TextUtils.isEmpty(address) && !TextUtils.isEmpty(postalCode) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) &&
                !TextUtils.isEmpty(cardNumber) && !TextUtils.isEmpty(cardname) &&
                !TextUtils.isEmpty(expiryDate) && !TextUtils.isEmpty(cvv);
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (!expiryDate.matches("\\d{4}")) {
            return false;
        }

        int month = Integer.parseInt(expiryDate.substring(0, 2));
        int year = Integer.parseInt(expiryDate.substring(2));

        if (month < 1 || month > 12) {
            return false;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;

        if (year < currentYear || (year == currentYear && month < (Calendar.getInstance().get(Calendar.MONTH) + 1))) {
            return false;
        }

        return true;
    }

}
