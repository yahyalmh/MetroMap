//package dao
//
//import android.content.Context
////import android.database.Cursor
////import android.database.sqlite.*
//import utils.MetroUtil
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
///**
// * Created by yaya-mh on 19/01/2019 16:07
// */
//class DbHelper: SQLiteOpenHelper {
//
//    companion object {
//        var dbName = "smartMetro.db"
//        var dbPath :String ?= null
//    }
//    private var mContext: Context
//    lateinit var mDatabase : SQLiteDatabase
//
//    constructor(context: Context) :super(context,dbName, null, 1 ){
////        dbPath = context.applicationInfo.dataDir + "/databases/"
//        dbPath = ApplicationLoader.getFilesDirFixed().absolutePath + "/databases/"
//        this.mContext = context
//    }
//
//    public fun createDatabase(){
//        val dbFile = File(dbPath+ dbName)
//        if (!dbFile.exists()){
////            this.readableDatabase
////            this.close()
//            try {
////                copyDBFromAsset(dbFile)
////                copyDataBase()
//                MetroUtil.copyDatabasesFromAssets()
//            }catch (e:IOException){
//
//                println("error when copy database to /database from asset")
//            }
//        }
//    }
//    public fun openDatabase(): Boolean{
//        val mPath = dbPath + dbName
//        mDatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE)
//        return mDatabase != null
//    }
//
//    override fun  close() {
//
//        mDatabase.close()
//        super.close()
//    }
//
//
//
//    private fun copyDBFromAsset(dbFile: File) {
//        /*if (!dbPath.exists()){
//            dbDir.mkdir()
//        }*/
//        if (!dbFile.exists()){
//            dbFile.createNewFile()
//            support.component.AndroidUtilities.copyFile(ApplicationLoader.applicationContext.assets.open(dbName), dbFile)
//        }
//    }
//
//    fun executeQuery(sql : String): Cursor{
//        android.os.Debug.waitForDebugger()
//
//        createDatabase()
//        openDatabase()
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(sql, null)
//        close()
//        return cursor
//
//    }
//
//    @Throws(IOException::class)
//    private fun copyDataBase() {
//        val mInput = mContext.assets.open(dbName)
//        val outFileName = dbPath + dbName
//        val mOutput = FileOutputStream(outFileName)
//        val mBuffer = ByteArray(1024)
//        var mLength: Int
//        mLength = mInput.read(mBuffer)
//        while (mLength > 0) {
//            mOutput.write(mBuffer, 0, mLength)
//            mLength = mInput.read(mBuffer)
//        }
//        mOutput.flush()
//        mOutput.close()
//        mInput.close()
//    }
//
//    override fun onCreate(p0: SQLiteDatabase?) {
//    }
//
//    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}