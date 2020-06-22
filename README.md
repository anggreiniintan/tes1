### Change

```
Buka ScanCode.java, baris 36-39 :


Intent intent = new Intent(getApplicationContext(), Main2Activity.class); // inisialisasi intent baru dengan screen Main2Activity.java
intent.putExtra("ruangan",Scann.resulttextView.getText().toString()); // melempar data ke screen Main2Activity.java
startActivity(intent); // Launch screen Main2Activity.java

Buka Main2Activity.java, baris (24-35)
Intent intent = getIntent(); 
//inisialisasi intent
String ruangan = intent.getStringExtra("ruangan"); 
//mengambil data yang dilempar dari screen ScanCode.java dengan index "ruangan"
x = "0";
y = "0";

// cek ruangan
if(ruangan.equals("D201")){
    x="120";
    y = "120";
}
else{
    x = "-99";
    y = "-99";
}

// Alternatif untuk dapetin data hasil scann QR dan di lempar ke screen selanjutnya. Udah di test dan jalan
```
