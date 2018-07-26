package com.example.jungdahee.aduehome;

        import android.app.Activity;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.ClipData;
        import android.content.Intent;
        import android.graphics.Typeface;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //private ImageButton btn_Connect;

    /**activity_main.xml에 나타나는 메뉴 오버플로우 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu_main.xml 파일을 java 객체로 인플레이트(inflate)해서 menu객체에 추가
        inflate정적인 객체(menu_main.xml)를 view에 전달*/
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**오버플로우를 클릭했을 때 발생하는 오버플로우 화면**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //menu_main.xml에서 지정한 id를 가져와 환경설정으로 설정
        switch (item.getItemId()){
            case R.id.menu_settings: //환경설정
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**bluetooth를 사용하기 위한 선언들**/
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mDevice = null;
    BluetoothSocket mSocket = null;

    Button mbtnConnect, mbtnLed1, mbtnLed2, mbtnLed3;

    private static final int ATIVA_BLUETOOTH = 1;
    private static final int ATIVA_CONNECT = 2;

    /**bluetooth에서 사용하는 쓰레드 선언**/
    ConnectedThread connectedThread;

    boolean connect = false;

    private static String MAC = null;

    /**아두이노와 통신하기 위한 UUID 설정**/
    UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this,SplashActivity.class)); //가장 먼저 실행해야하는 activity인 SplashActivity 실행

        super.onCreate(savedInstanceState); //하위 클래스에서는 반드시 상위 클래스의 생성자를 사용해야함
        setContentView(R.layout.activity_main); //activity_main을 화면에 띄우주는 구문

        //btn_Connect = (ImageButton) findViewById(R.id.btn_connect);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //default인 bluetooth를 가져와 사용하고자 하는 변수에 assign

        final int[] a = {0};
        final int[] b = {0};
        final int[] c = {0};
        final int[] d = {0};
        final int[] e = {0};
        final int[] f = {0};
        final int[] g = {0};
        final int[] h = {0};

        /**activity_main에서 설정해놓은 id를 찾아 변수에 저장하여 사용하게 함 **/
        final ImageButton mbtnAir = (ImageButton) findViewById(R.id.btnAir);
        final ImageButton mbtnConnect = (ImageButton) findViewById(R.id.btnConnect);
        final ImageButton mbtnCurtain = (ImageButton) findViewById(R.id.btnCurtain);
        final ImageButton mbtnDoor = (ImageButton) findViewById(R.id.btnDoor);
        final ImageButton mbtnElectric = (ImageButton) findViewById(R.id.btnElectric);
        final ImageButton mbtnFrontdoor = (ImageButton) findViewById(R.id.btnFrontdoor);
        final ImageButton mbtnGas = (ImageButton) findViewById(R.id.btnGas);
        final ImageButton mbtnHelp = (ImageButton) findViewById(R.id.btnHelp);
        final ImageButton mbtnIluminate = (ImageButton) findViewById(R.id.btnIluminate);

        /**사용가능한 블루투스가 없는 상태일 때**/
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스 연결해주세요", Toast.LENGTH_LONG).show(); //Toast 메세지를 통해 블루투스를 연결해야함을 사용자에게 안내
        } else if (!mBluetoothAdapter.isEnabled()) { //연결가능한 블루투스가 있을 경우 허가 요청을 함
            Intent activaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //요청
            startActivityForResult(activaBluetooth, ATIVA_BLUETOOTH); //선언한 activityBluetooth와 ATIVA_BLUETOOTH를 파라미터로 실행, 실행 결과를 넘겨 activity로부터 실행결과를 가져옴
       }

       /**activity_main.xml에서 가져온 버튼 아이디로부터 새로 선언한 버튼들을 실행시킴(버튼을 눌렀을 때 실행하는 것)**/
        mbtnConnect.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) { //2번째 버튼인 mbtnConnect를 클릭하였을 때 Listener로 이벤트를 받아서 처리함
                mbtnConnect.setBackgroundColor(getResources().getColor(R.color.pressed)); //버튼이 클릭되었을 때 버튼의 색깔이 변경됨(색이 변경된 것으로 On/Off를 암시적으로 표현
                if (connect) { //블루투스가 연결되면 try-catch문 실행

                    try { //socket은 안드로이드와 디바이스의 블루투스를 연결하는 지점
                        mSocket.close(); //실행하기 전 종료 후 실행해야함
                        connect = false; //connect를 false로 설정
                        Toast.makeText(getApplicationContext(), "Bluetooth for disconnected", Toast.LENGTH_SHORT).show(); //블루투스가 연결되지 않음을 안내

                    } catch (IOException e) { //try에서 오류가 있는 부분을 catch문에서 처리하게 됨
                        Toast.makeText(getApplicationContext(), "um Error: " + e, Toast.LENGTH_SHORT).show();

                    }

                } else { //블루투스가 연결되지 않았을 경우
                    Intent ableList = new Intent(MainActivity.this, ConnectDialogActivity.class); //ConnectDialogActivity의 클래스를 실행하도록 Intent로 사용
                    startActivityForResult(ableList, ATIVA_CONNECT); // startActivityForResult 실행 결과를 가져옴
                }

            }
        });

        /**두번째 버튼을 클릭했을 때 이벤트 처리, 오늘의 날씨를 알려주는 웹사이트로 연결**/
        mbtnAir.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view){
                if (a[0] ==0){
                    mbtnAir.setBackgroundColor(getResources().getColor(R.color.pressed)); //버튼 클릭했을 때 색깔이 바뀜

                    //사용한 url을 ACTION_VIEW에 연결하여 사용자에게 새창이 뜨며 웹사이트로 연결
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://weather.naver.com/rgn/cityWetrMain.nhn"));
                    startActivity(myIntent);

                    mbtnAir.setBackgroundColor(getResources().getColor(R.color.nonpressed1)); // 바로 버튼 색이 바뀌게 함

                }

            }
        });


        /**세번째 버튼을 눌렀을 때 이벤트 처리, 연결된 디바이스에 시그널을 보내서 통신함, 커튼 작동 소스**/
        mbtnCurtain.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view){
                if (connect) { //블루투스가 연결되었을 때
                    if (b[0] == 0) {
                        connectedThread.enviar("1"); //쓰레드메소드를 실행하여 1이라는 시그널을 파라미터로 전달

                        mbtnCurtain.setBackgroundColor(getResources().getColor(R.color.pressed));
                        b[0] = 1;
                    } else if (b[0] == 1) { //다시 클릭했을 경우 Off 구현
                        connectedThread.enviar("2"); //2라는 신호를 쓰레드로 보냄

                        mbtnCurtain.setBackgroundColor(getResources().getColor(R.color.nonpressed3));
                        b[0] = 0;
                    }
                }

            }
        });

        /**네번째 버튼을 눌렀을 때 이벤트 처리, 문 작동 소스**/
        mbtnDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect) { //연결되었을 때
                    if (c[0] == 0) {
                        connectedThread.enviar("3"); //3이라는 시그널을 전달

                        mbtnDoor.setBackgroundColor(getResources().getColor(R.color.pressed));
                        c[0] = 1;
                    } else if (c[0] == 1) {
                        connectedThread.enviar("4"); //4라는 시그널을 전달

                        mbtnDoor.setBackgroundColor(getResources().getColor(R.color.nonpressed4));
                        c[0] = 0;
                    }
                }
            }
        });

        /**다섯번째 버튼을 눌렀을 때 이벤트 처리**/
        mbtnElectric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect) { //연결되었을 때
                    if (d[0] == 0) {

                        mbtnElectric.setBackgroundColor(getResources().getColor(R.color.pressed));
                        d[0] = 1;
                    } else if (d[0] == 1) {

                        mbtnElectric.setBackgroundColor(getResources().getColor(R.color.nonpressed5));
                        d[0] = 0;
                    }
                }
            }
        });

        /**여섯번째 버튼을 눌렀을 때 이벤트 처리, 현관문 작동 소스**/
        mbtnFrontdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect) { //연결되었을 때
                    if (e[0] == 0) {
                        connectedThread.enviar("5"); //5라는 시그널을 보냄

                        mbtnFrontdoor.setBackgroundColor(getResources().getColor(R.color.pressed));
                        e[0] = 1;
                    } else if (e[0] == 1) {
                        connectedThread.enviar("6"); //재클릭시 6이라는 신호를 보냄

                        mbtnFrontdoor.setBackgroundColor(getResources().getColor(R.color.nonpressed6));
                        e[0] = 0;
                    }
                }
            }
        });

        /**일곱번째 버튼을 눌렀을 때 이벤트 처리, 가스밸브 작동 소스**/
        mbtnGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect) { //연결되었을 때
                    if (f[0] == 0) {
                        connectedThread.enviar("7"); //7이라는 신호를 보냄

                        mbtnGas.setBackgroundColor(getResources().getColor(R.color.pressed));
                        f[0] = 1;
                    } else if (f[0] == 1) {
                        connectedThread.enviar("8"); //8이라는 시그널을 보냄

                        mbtnGas.setBackgroundColor(getResources().getColor(R.color.nonpressed7));
                        f[0] = 0;
                    }
                }
            }
        });

        /**여덟번째 버튼을 눌렀을 때 이벤트 처리, 개발자에게 문의하기 소스**/
        mbtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (g[0] ==0){
                    mbtnHelp.setBackgroundColor(getResources().getColor(R.color.pressed));

                    //SendEmailActivity를 인텐트를 통해 전달
                    Intent myIntent = new Intent(MainActivity.this, SendEmailActivity.class);
                    startActivity(myIntent); //실행
                }
                mbtnHelp.setBackgroundColor(getResources().getColor(R.color.nonpressed8));
            }
        });


        /**아홉번째 버튼을 눌렀을 때 이벤트 처리, 조명 작동 소스**/
        mbtnIluminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect) { //연결되었을 때
                    if (h[0] == 0) {
                        connectedThread.enviar("9"); //9라는 신호 전달

                        mbtnIluminate.setBackgroundColor(getResources().getColor(R.color.pressed));
                        h[0] = 1;
                    } else if (h[0] == 1) {
                        connectedThread.enviar("0"); //0이라는 신호 전달

                        mbtnIluminate.setBackgroundColor(getResources().getColor(R.color.nonpressed9));
                        h[0] = 0;
                    }
                }
            }
        });

    }

    /**액티비티 실행결과를 가져오는 메소드**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ATIVA_BLUETOOTH: //블루투스
                if (resultCode == Activity.RESULT_OK) { //실행 결과 성공일 경우
                    Toast.makeText(getApplicationContext(), "블루투스 연결 성공", Toast.LENGTH_SHORT).show(); //연결 성공을 알려주는 토스트

                } else { //실행 결과 실패일 경우
                    Toast.makeText(getApplicationContext(), "블루투스 연결 실패", Toast.LENGTH_SHORT).show(); //연결 실패를 알려주는 토스트
                    finish(); //종료
                }
                break;

            case ATIVA_CONNECT: //블루투스 연결
                if (resultCode == Activity.RESULT_OK) { //실행 결과 성공일 경우
                    MAC = data.getExtras().getString(ConnectDialogActivity.ENDERECO_MAC); //ConnectDialogActivity에서 ENDERECO_MAC를 가져와 MAC이라는 변수에 assign

                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC); //디바이스에 MAC주소와 연결된 블루투스 어댑터를 assign

                    try {
                        mSocket = mDevice.createRfcommSocketToServiceRecord(MEU_UUID); //MEU_UUID에 선언한 UUID로 소켓 생성

                        mSocket.connect(); //소켓을 연결하는 메소드

                        connect = true; //연결완료

                        connectedThread = new ConnectedThread(mSocket); //connectedThread에 생성한 소켓을 새로 생성하여 assign

                        connectedThread.start(); //소켓 실행

                        Toast.makeText(getApplicationContext(), "com: " + MAC, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {

                        connect = false;

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "false of MAC", Toast.LENGTH_SHORT).show(); //실패를 알려주는 토스트
                }

        }
    }

    /**블루투스를 실행해주는 쓰레드**/
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        /**통신을 하기 위한 쓰레드**/
        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            /**클라이언트와 소통을 하기 위한 stream 생성**/
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**사용할 데이터를 버퍼형으로 변경해주는 메소드**/
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
        }

        /**연결된 디바이스에게 전달할 data를 버퍼형으로  바꾸어 전달하게 함 */
        public void enviar(String dadosEnviar) {
            byte[] msgBuffer = dadosEnviar.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
            }
        }
    }
}
