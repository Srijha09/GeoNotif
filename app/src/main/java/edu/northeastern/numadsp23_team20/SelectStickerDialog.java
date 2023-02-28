package edu.northeastern.numadsp23_team20;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Arrays;
import java.util.List;

public class SelectStickerDialog extends AppCompatDialogFragment {

    List<String> stickers;
    AlertDialog.Builder builder;
    SelectStickerListener selectStickerListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.stickers = Arrays.asList(
                "pikachu", "mewtwo", "charmander", "squirtle", "groudon", "jolteon"
        );
        this.builder = new AlertDialog.Builder(getActivity());
        this.builder.setTitle("Select a sticker");
        View view = getActivity().getLayoutInflater().inflate(R.layout.select_sticker_dialog, null);
        for (String sticker: this.stickers) {
            view.findViewById(this.getId(sticker)).setOnClickListener(v -> {
                selectStickerListener.applyInformation(sticker, v.getId());
            });
        }
        this.builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.selectStickerListener = (SelectStickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("onAttach error: " + context.toString());
        }
    }

    private int getId(String sticker) {
        switch (sticker) {
            case "pikachu":
                return R.id.pikachu;
            case "mewtwo":
                return R.id.mewtwo;
            case "charmander":
                return R.id.charmander;
            case "squirtle":
                return R.id.squirtle;
            case "groudon":
                return R.id.groudon;
            case "jolteon":
                return R.id.jolteon;
        }
        return 0;
    }

    public interface SelectStickerListener {
        void applyInformation(String selectedStickerName, int selectedStickerId);
    }
}
