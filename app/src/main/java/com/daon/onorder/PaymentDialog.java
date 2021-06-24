package com.daon.onorder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PaymentDialog extends Dialog {
    private Context context;
    private PaymentDialogClickListener paymentDialogClickListener;
    private LinearLayout llCard, llPay, llGift;

    public PaymentDialog(@NonNull Context context, PaymentDialogClickListener paymentDialogClickListener) {
        super(context);
        this.context = context;
        this.paymentDialogClickListener = paymentDialogClickListener;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_paymentlayout);

        llCard = findViewById(R.id.dialog_card);
        llPay = findViewById(R.id.dialog_pay);
        llGift = findViewById(R.id.dialog_gift);

        llCard.setOnClickListener(v -> {
            // 저장버튼 클릭
            this.paymentDialogClickListener.onCardClick();
            dismiss();
        });
        llPay.setOnClickListener(v -> {
            // 취소버튼 클릭
            this.paymentDialogClickListener.onPayClick();
            dismiss();
        });

        llGift.setOnClickListener(v -> {
            // 취소버튼 클릭
            this.paymentDialogClickListener.onGiftClick();
            dismiss();
        });
    }
}
