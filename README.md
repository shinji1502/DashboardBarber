# BarberPro

Aplikasi desktop JavaFX dengan arsitektur MVC untuk tampilan login, dashboard, pelanggan, laporan, dan pengaturan.

## Struktur Project

- src/Main.java: entry point aplikasi
- src/controller/: controller MVC
- src/model/: model data
- src/database/: kelas database placeholder
- src/util/Navigator.java: navigasi antar halaman
- src/view/: file FXML
- src/css/style.css: stylesheet dasar

## Jalankan Tanpa Maven/Gradle

1. Buka folder project di VS Code.
2. Pastikan Java JDK terinstall.
3. Jalankan perintah berikut dari terminal:

```powershell
javac --module-path "C:\Program Files\Java\jdk-9.0.4\jmods" --add-modules javafx.controls,javafx.fxml -d bin $(Get-ChildItem src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
java --module-path "C:\Program Files\Java\jdk-9.0.4\jmods" --add-modules javafx.controls,javafx.fxml -cp bin Main
```

Jika Anda ingin menggunakan VS Code Run and Debug, buka konfigurasi launch yang tersedia pada folder .vscode.
