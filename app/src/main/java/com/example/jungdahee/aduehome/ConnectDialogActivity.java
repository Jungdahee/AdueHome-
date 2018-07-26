package com.example.jungdahee.aduehome;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Jung dahee on 2017-08-24.
 */

/**ConnectDialogActivity는 ListActivity 상속받음**/
public class ConnectDialogActivity extends ListActivity {

    private BluetoothAdapter mBluetoothAdapter2 = null;     //블루투스 어댑터의 연결유무

    static String ENDERECO_MAC = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        //google에서 제공하는 심플 리스트를 사용하여 연결 가능한 디바이스를 보여주기 위해 선언

        mBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter(); //블루투스 객체를 가져와 mBluetoothAdapter2에 선언

        Set<BluetoothDevice> dispositivespreads = mBluetoothAdapter2.getBondedDevices(); //블루투스 기기의 리스트를 getBondedDevices() 메소드로 가져옴

        /**디바이스의 정보를 가져와 뷰에 표시함**/
        if (dispositivespreads.size() > 0) {
            for (BluetoothDevice dispositive : dispositivespreads){ //dispositivespreads에 저장되어 있는 리스트를 dispositive에 하나씩 넣음
                String nameBt = dispositive.getName(); //디바이스의 이름을 가져옴
                String macBt = dispositive.getAddress(); //디바이스의 주소를 가져옴
                ArrayBluetooth.add(nameBt + "\n" + macBt); //위에서 선언한 ArrayBluetooth에 추가
            }
        }
        setListAdapter(ArrayBluetooth); //사용할 어댑터를 정의
    }

    /**디바이스를 클릭했을 때 실행되는 메소드**/
    @Override
    /**클릭한 디바이스의 자세한 정보를 가져옴(몇번째에 위치하는지, id는 무엇인지 등)**/
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String information = ((TextView)v).getText().toString(); //View의 텍스트를 가져와 string형으로 변형해서 information에 assign

        String enterMac = information.substring(information.length() - 17 ); //information의 길이를 17이하를 설정

        /**메세지를 intent를 이용하여 MainActivity로 전달**/
        Intent returnMac = new Intent();
        returnMac.putExtra(ENDERECO_MAC,enterMac); //Mac주소를 돌려줌
        setResult(RESULT_OK, returnMac); //결과 넘겨줌
        finish(); //종료

    }
}