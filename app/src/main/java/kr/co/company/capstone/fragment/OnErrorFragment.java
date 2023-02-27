package kr.co.company.capstone.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.navigation.Navigation;
import io.reactivex.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OnErrorFragment extends AppCompatDialogFragment  {

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("!")
                .setMessage("에러가 발생했습니다.")
                .setPositiveButton("넵", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assert getParentFragment() != null;
                        Navigation.findNavController(Objects.requireNonNull(getParentFragment().getView())).popBackStack();
                    }
                });
        return builder.create();
    }

}
