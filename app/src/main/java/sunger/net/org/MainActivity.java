package sunger.net.org;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.sunger.net.view.CircularMenu;

import sunger.net.org.a.R;

public class MainActivity extends AppCompatActivity {
    private CircularMenu wheelMenuView;
    private EditText editText1, editText2, editText3, editText4, editText5, editText6;
    private Button button;
    private PanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wheelMenuView = (CircularMenu) findViewById(R.id.panview);
        //  wheelMenuView = new WheelMenuView(this);
//        setContentView(wheelMenuView);
//        wheelMenuView.setLineWidth(20);
//        wheelMenuView.setItemCount(6);
//        wheelMenuView.setBackgroundColor(Color.BLACK);
//        wheelMenuView.setPressedColor(Color.BLUE);
//        wheelMenuView.setRadius(300);
//        wheelMenuView.setInnerRadius(120);
//        wheelMenuView.setItemColor(Color.CYAN);
//        wheelMenuView.setCenterView(R.layout.center_layout);
//        wheelMenuView.setRadiusLineWidth(20);
//        wheelMenuView.setStartAngle(5);
        adapter = new PanAdapter();
        wheelMenuView.setAdapter(adapter);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //大圆半径
                float radius = Float.valueOf(editText1.getText().toString());
                float innerRadius = Float.valueOf(editText2.getText().toString());
                //线条宽度
                float lineWidth = Float.valueOf(editText3.getText().toString());
                //内圆线宽度
                float radiusLineWidth = Float.valueOf(editText4.getText().toString());
                int itemCount = Integer.valueOf(editText5.getText().toString());
                int startAngle = Integer.valueOf(editText6.getText().toString());
                wheelMenuView.setInnerRadius(innerRadius);
                wheelMenuView.setLineWidth(lineWidth);
                wheelMenuView.setItemCount(itemCount);
                wheelMenuView.setStartAngle(startAngle);
                wheelMenuView.setRadiusLineWidth(radiusLineWidth);
                wheelMenuView.setRadius(radius);
            }
        });

    }
}
