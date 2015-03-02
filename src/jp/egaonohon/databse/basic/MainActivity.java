package jp.egaonohon.databse.basic;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*
 * MainActivity
 * SimpleDatabaseHelper
 *
 */

public class MainActivity extends Activity {
	private SimpleDatabaseHelper helper = null;// このメンバ変数は、SimpleDatabaseHelperクラスの参照になっている。
	private EditText txtIsbn = null;// ISBNを表示
	private EditText txtTitle = null;// タイトルを表示
	private EditText txtPrice = null;// 価格を表示

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * データベースの構築及び、オープン処理を実施。 DBを作るときに役に立つクラスがこれ。SimpleDatabaseHelper
		 * SQLiteオープンヘルパーを継承して作る。
		 */
		helper = new SimpleDatabaseHelper(this);

		/*
		 * 参照を作っておいている。
		 */
		txtIsbn = (EditText) findViewById(R.id.txtIsbn);
		txtTitle = (EditText) findViewById(R.id.txtTitle);
		txtPrice = (EditText) findViewById(R.id.txtPrice);
	}

	/*
	 * レコードを追加する場合に使うメソッド。SQLのINSERTに相当。
	 */
	public void onSave(View view) { // レイアウト側にボタンをonClickを設定しているのでfindViewByIdはしていない。
		// 書き込み・読み込み用のデータベースオブジェクトgetWritableDatabaseを取得。一番最初に取得する。newではない。
		// dbはすでにあるDBの参照。
		SQLiteDatabase db = helper.getWritableDatabase();
		// カラム（？　これだと行になるが列では？）名とデータの組わせで1レコードのデータを作成している
		// ContentValuesでは、キーが項目名になる。cvがレコードのフォーマットに合わせる入れ物。HashMapみたいなものかな？
		ContentValues cv = new ContentValues();
		cv.put("isbn", txtIsbn.getText().toString());// キーとデータの組み合せで入れていく（.put)
		cv.put("title", txtTitle.getText().toString());
		cv.put("price", txtPrice.getText().toString());
		// レコードの追加を実施する
		// 第一引数：テーブル名
		// 第二引数：nullColumHack(項目がnullの場合の処理方法の指定)。通常はあまり考えられない。
		// 第三引数：ContentValues(レコードデータ)
		// 戻り値：long型　row　idが返却される
		long id = db.insert("books", null, cv);// SQLのINSERTに相当するinsert()メソッドで追加される。引数は、テーブル名とレコードが入っているcv。
		String msg = "";
		if (id != -1) {// 戻り値を確認して成否を確認。戻り値-1の時は、テーブルがないなどの異常時。
			msg = "データの登録に成功しました。";
		} else {
			msg = "データの登録に失敗しました。";
		}
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		/*
		 * もちろん、SQL文を直接送信もできる。 db.execSQL(※SQL文入る)
		 */
	}

	/*
	 * public void onSave(View view) { SQLiteDatabase db =
	 * helper.getWritableDatabase(); ContentValues cv = new ContentValues();
	 * cv.put("isbn", txtIsbn.getText().toString()); cv.put("title",
	 * txtTitle.getText().toString()); cv.put("price",
	 * txtPrice.getText().toString()); db.insertWithOnConflict("books", null,
	 * cv, SQLiteDatabase.CONFLICT_REPLACE); Toast.makeText(this,
	 * "データの登録に成功しました。", Toast.LENGTH_SHORT).show(); }
	 */

	/*
	 * public void onSave(View view) { SQLiteDatabase db =
	 * helper.getWritableDatabase(); ContentValues cv = new ContentValues();
	 * cv.put("title", txtTitle.getText().toString()); cv.put("price",
	 * txtPrice.getText().toString()); String[] params = {
	 * txtIsbn.getText().toString() }; db.update("books", cv, "isbn = ?",
	 * params); Toast.makeText(this, "データの登録に成功しました。",
	 * Toast.LENGTH_SHORT).show(); }
	 */

	public void onDelete(View view) {// レイアウト側にボタンをonClickを設定しているのでfindViewByIdはしていない。
		// ISBNコード　EditTextに入れられた文字列を取得。ISBNを軸に削除作業を行う。
		String[] params = { txtIsbn.getText().toString() };
		// 書き込み・読み込み用のデータベースオブジェクトを取得。newじゃないのか。
		SQLiteDatabase db = helper.getWritableDatabase();
		// レコード削除する
		// 第一引数：テーブル名
		// 第二引数：Where句に相当する。検索条件。
		// 第三引数：Where句の指定データ
		// 戻り値は、影響を受けた行数。このパターンだとISBNの重複はないので1が戻り値。
		int ct = db.delete("books", "isbn = ?", params);// 第1引数はテーブル名。第2引数は軸とするISBN。第三引数がisbnが入った配列。
		Toast.makeText(this, ct + "件のデータの削除に成功しました。", Toast.LENGTH_SHORT)
				.show();
	}

	public void onSearch(View view) {
		// 読み込み用のデータベースオブジェクトを取得。SQLの読み込みは結果が戻ってきてそれを処理するので重い作業になる。
		SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
		// db.queryの第二引数を作る
		// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
		String[] cols = { "isbn", "title", "price" };
		// Select文の行を特定する（where句）文字列を取得（ISBNの指定）。ISBNを引っ張ってこいよ!て感じ。
		String[] params = { txtIsbn.getText().toString() };
		// 実際にselect文に相当するメソッドを実行。以下、query()の引数。
		// 第一引数：テーブル名
		// 第二引数：項目名（カラム名）の指定（select　【＊】に相当）
		// 第三引数：Where句に相当する
		// 第四引数：Where句の指定データ
		// 第五引数：GropuBY句に相当。何かのカテゴリ単位で引っ張ってくるときに記述。
		// 第六引数：Having句に相当(GroupについてWhere句)
		// 第七引数：Ｏｒｄｅｒ　ＢＹ句に相当する
		// 第八引数：ＬＩＭＩＴ句に相当する。1000件中の10件だけを…などの指定。
		// 戻り値は、返却されたレコード群を示すCursor（カーソル）が返却される
		Cursor cs = db.query("books", cols, "isbn = ?", params, null, null,
				null, null);// DBからの戻り値のCursorをこの後扱っていく。
		// データがあれば、データを取得する。なければ、無い！
		if (cs.moveToFirst()) {
			// データがあれば、それを取得する
			txtTitle.setText(cs.getString(1));// タイトルを引っ張ってくる。(1)は列を示す。一番左が0から始まる。0はisbnね。
			int price = cs.getInt(2);// 価格を引っ張ってくる
			// String strPrice=cs.getString(2);
			// Toast.makeText(this, strPrice,
			// Toast.LENGTH_SHORT).show();
			txtPrice.setText(Integer.toString(price));
		} else {
			// データがなかったので、その旨を表示する
			Toast.makeText(this, "データがありません。", Toast.LENGTH_SHORT).show();
		}

		/*
		 * 検索時にSQL文を直接送る場合はちょっと構文が違う。DELETEやINSERT時のようなexecSQLではない。 例えばこんな感じ。
		 * db.rawQuery("select * from books where",null);
		 */

		/*
		 * String msg = ""; boolean eol = cs.moveToFirst(); while (eol) { msg +=
		 * cs.getString(1); eol = cs.moveToNext(); } Toast.makeText(this, msg,
		 * Toast.LENGTH_SHORT).show();
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
