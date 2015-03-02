package jp.egaonohon.databse.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * SimpleDatabaseHelperは、スマホ内のアプリで自分が使いたいデータベースを構築するのSQLiteOpenHelperを継承して作ったクラス。
 * SQLiteOpenHelperを継承してこのクラスを作っている。
 */
public class SimpleDatabaseHelper extends SQLiteOpenHelper {
	// データベースファイル名に制限事項はありません。
	static final private String DBNAME = "sample.sqlite";// 名前は何でもいいし拡張子がなくてもいいが今回はsample.sqliteにしている。
	/*
	 * この番号はフレームワーク側で利用されています。バージョンとはSQLiteOpenHelperが見ている数字。
	 * データベースを改修（レコードを追加するなど）するときにこのバージョン番号を上げるとデータベースを作りなおしてくれる。しかも自動で。
	 * ただし、バージョンナンバーは下げられない。上げる。下げるとエラー。
	 */
	static final private int VERSION = 1;

	public SimpleDatabaseHelper(Context context) {
		// SQLiteOpenHelperのコンストラクタ呼出し。この辺はお決まりの呪文みたいなもの。
		super(context, DBNAME, null, VERSION);
	}

	// 検索ではSELECT分を使用。

	/*
	 * このスマホに初めてアプリを入れたら
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	// アクティビティ側でgetWritableDatabase();か、getReadableDatabase();でデータベースを呼ばれたら
	// 初めて呼ばれたタイミングで、DBがないときにこのonCreateが呼び出される!!　特にアクティビティ側で呼び出すような記述をしなくていい。
	//それがSQLiteOpenhelperの役目。
	//ただし、一度テーブルを作ると二度とこのメソッドは呼び出されない。
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE books ("
				+ "isbn TEXT PRIMARY KEY, title TEXT, price INTEGER)");//isbnをキーにして、タイトルとプライスを入れる。
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('111-1-1111-1111-1', 'Android入門1', 1000)");//ここでは初期データも同時に突っ込んでいる以下同様。
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('222-2-2222-2222-2', 'Android入門2', 2000)");//ただ、普通はこういうことを最初にやることはあまりないはず。
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('333-3-3333-3333-3', 'Android入門3', 3000)");
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('444-4-4444-4444-4', 'Android入門4', 4000)");
		db.execSQL("INSERT INTO books(isbn, title, price)"
				+ " VALUES('555-5-5555-5555-5', 'Android入門5', 5000)");
	}

	/*
	 * @Override public void onCreate(SQLiteDatabase db) {
	 * db.execSQL("CREATE TABLE books (" +
	 * "isbn TEXT PRIMARY KEY, title TEXT, price INTEGER)"); String[] isbns = {
	 * "111-1-1111-1111-1", "222-2-2222-2222-2", "333-3-3333-3333-3",
	 * "444-4-4444-4444-4", "555-5-5555-5555-5" }; String[] titles = {
	 * "Android入門1", "Android入門2", "Android入門3", "Android入門4", "Android入門5" };
	 * int[] prices = { 1000, 2000, 3000, 4000, 5000 }; db.beginTransaction();
	 * try { SQLiteStatement sql = db.compileStatement(
	 * "INSERT INTO books(isbn, title, price) VALUES(?, ?, ?)"); for (int i = 0;
	 * i < isbns.length; i++) { sql.bindString(1, isbns[i]); sql.bindString(2,
	 * titles[i]); sql.bindLong(3, prices[i]); sql.executeInsert(); }
	 * db.setTransactionSuccessful(); } finally { db.endTransaction(); } }
	 */

	/*
	 *  このメソッドonUpgradeはコンストラクタでバージョンが変更されているときにだけ自動的に呼び出される。
	 *  ただ、事故の元なのでこのメソッドは慎重に使うべき。
	 *  バージョン1→バージョン5など。バージョン1つずつ上げてもらえるとは限らない。
	 *
	 *  したがって、最初のDBのテーブルを作るときに、予備の列を用意しておくなど安全策をとっておく方法も。
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) { //第2引数が古いバージョン番号。第3引数が新しいバージョン番号。
		// 単純に古いデータベースファイルを削除しただけ
		db.execSQL("DROP TABLE IF EXISTS books");
		// 本当はバックアップ等をする必要がある!!　要注意!!
		onCreate(db);
		// 本当はこの後で、バックアップしたデータを戻す作業をする必要がある！！！

	}

}
