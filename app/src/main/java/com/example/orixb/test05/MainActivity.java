package com.example.orixb.test05;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity  extends Activity implements OnClickListener {

    private final static String DB_NAME = "test.db"; // データベース名
    private final static String DB_TABLE = "product"; // テーブル名
    private final static int DB_VERSION = 1; // バージョン

    DBHelper helper = null;
    SQLiteDatabase db = null;

    int[] BUTTONS = { R.id.ins, R.id.up, R.id.del, R.id.show };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタンオブジェクト取得
        for (int btnId : BUTTONS) {
            Button btn = (Button) findViewById(btnId);
            btn.setOnClickListener(this);
        }

        // DB作成
        helper = new DBHelper(MainActivity.this);

    }

    @Override
    public void onClick(View v) {

        // メッセージ用
        String message = "";
        TextView res = (TextView) findViewById(R.id.res);

        // EditTextオブジェクトの取得
        EditText editId = (EditText) findViewById(R.id.s_id);
        EditText editName = (EditText) findViewById(R.id.s_name);
        EditText editPrice = (EditText) findViewById(R.id.s_price);

        // テーブルレイアウトオブジェクトの取得
        TableLayout t_layout = (TableLayout) findViewById(R.id.list);

        // テーブルレイアウトオブジェクトのクリア
        t_layout.removeAllViews();

        // 該当DBオブジェクトの取得
        db = helper.getWritableDatabase();


        // 登録ボタン押下時
        if (v.getId() == R.id.ins) {
            // データ登録
            try {

                // トランザクション制御の開始
                db.beginTransaction();

                // 登録データ設定
                ContentValues val = new ContentValues();
                val.put("productid", editId.getText().toString());
                val.put("name", editName.getText().toString());
                val.put("price", editPrice.getText().toString());

                // データ登録
                db.insert(DB_TABLE, null, val);

                // コミット
                db.setTransactionSuccessful();

                // トランザクション制御終了
                db.endTransaction();

                // EditTextの表示を初期化
                editId.setText("");
                editName.setText("");
                editPrice.setText("");

                // メッセージ設定
                message = "データ登録が完了しました";

            } catch (Exception e) {
                // メッセージ設定
                message = "データ登録に失敗しました";
                Log.e("登録エラー", e.toString());

            }


            // 更新ボタン押下時
        } else if (v.getId() == R.id.up) {
            try {
                // トランザクション制御開始
                db.beginTransaction();

                // 更新データ設定
                ContentValues val = new ContentValues();
                val.put("name", editName.getText().toString());
                val.put("price", editPrice.getText().toString());

                // データ更新
                db.update(DB_TABLE, val, "productid=?", new String[] { editId.getText().toString() });

                // コミット
                db.setTransactionSuccessful();

                // トランザクション制御終了
                db.endTransaction();

                // メッセージ設定
                message = "データの更新をしました";

            } catch (Exception e) {
                message = "更新に失敗しました";
                Log.e("更新", e.toString());
            }


            // 削除ボタン押下時
        } else if (v.getId() == R.id.del) {
            try {
                // トランザクション開始
                db.beginTransaction();

                // データ削除
                db.delete(DB_TABLE, "productid=?", new String[] { editId.getText().toString() });

                // コミット
                db.setTransactionSuccessful();

                // トランザクション制御終了
                db.endTransaction();

                // メッセージ設定
                message = "データを削除しました";

            } catch (Exception e) {
                message = "削除に失敗しました";
                Log.e("削除", e.toString());

            }

            // 表示ボタン押下時
        } else if (v.getId() == R.id.show) {
            try {
                // データの取得
                db = helper.getReadableDatabase();

                // 列名の定義
                String[] columns = { "productid", "name", "price" };

                // データの取得
                Cursor cursor = db.query(DB_TABLE, columns, null, null, null,
                        null, "productid");

                // テーブルレイアウトの表示範囲の設定
                t_layout.setStretchAllColumns(true);


                // テーブル一覧のヘッダ部設定
                TableRow headrow = new TableRow(MainActivity.this);
                TextView headeTxt1 = new TextView(MainActivity.this);
                headeTxt1.setText("商品ID");
                headeTxt1.setGravity(Gravity.CENTER_HORIZONTAL);
                headeTxt1.setWidth(60);
                TextView headText2 = new TextView(MainActivity.this);
                headText2.setText("商品名");
                headText2.setGravity(Gravity.CENTER_HORIZONTAL);
                headText2.setWidth(100);
                TextView headText3 = new TextView(MainActivity.this);
                headText3.setText("価格");
                headText3.setGravity(Gravity.CENTER_HORIZONTAL);
                headText3.setWidth(60);
                headrow.addView(headeTxt1);
                headrow.addView(headText2);
                headrow.addView(headText3);
                t_layout.addView(headrow);


                // 取得したデータをテーブル明細部に設定
                while (cursor.moveToNext()) {
                    TableRow row = new TableRow(MainActivity.this);
                    TextView productIdText = new TextView(MainActivity.this);
                    productIdText.setText(cursor.getString(0));
                    productIdText.setGravity(Gravity.CENTER_HORIZONTAL);

                    TextView nameText = new TextView(MainActivity.this);
                    nameText.setText(cursor.getString(1));
                    nameText.setGravity(Gravity.CENTER_HORIZONTAL);

                    TextView prictTextView = new TextView(MainActivity.this);
                    prictTextView.setText(cursor.getString(2));
                    prictTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                    row.addView(productIdText);
                    row.addView(nameText);
                    row.addView(prictTextView);
                    t_layout.addView(row);

                    // メッセージの設定
                    message = "データ取得をしました";

                }
            } catch (Exception e) {
                message = "データ取得に失敗しました";
                Log.e("表示", e.toString());
            }

        }

        // DBオブジェクトクローズ
        db.close();

        // メッセージ表示
        res.setText(message);

    }

    // データベースヘルパーの定義
    private static class DBHelper extends SQLiteOpenHelper {
        // コンストラクタ定義
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        // データベースの生成時処理
        @Override
        public void onCreate(SQLiteDatabase db) {
            // テーブルの作成
            // SQL文の定義
            String sql = "create table if not exists "
                    + DB_TABLE
                    + "(productid text not null,name text not null,price integer default 0)";
            // SQL実行
            db.execSQL(sql);
        }

        // データベースのアップグレード時の処理
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // テーブル削除
            db.execSQL("drop table if exists " + DB_TABLE);
            // テーブル作成
            onCreate(db);
        }
    }
}