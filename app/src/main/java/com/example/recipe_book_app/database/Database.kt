package com.example.recipe_book_app.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.recipe_book_app.database.model.CategoryModel
import com.example.recipe_book_app.database.model.IngredientModel
import com.example.recipe_book_app.database.model.RecipeModel
import com.example.recipe_book_app.database.model.User

class Database(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME: String = "users_db"
        private const val DB_VERSION: Int = 41
        private const val TABLE_USER = "user"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_EMAIL = "email"
        private const val USER_PASSWORD = "pass"
        private const val USER_TYPE = "type"

        // category table
        private const val TABLE_CATEGORY = "category"
        private const val CATEGORY_ID = "cat_id"
        private const val CATEGORY_TITLE = "title"
        private const val IMG_FOREIGN_ID= "img_foreign_id"
        //  private const val CATEGORY_NUM_OF_RECIPES= "num_of_recipes"

        // recipe table
        private const val TABLE_RECIPE = "recipe"
        private const val RECIPE_ID = "recipe_id"
        private const val RECIPE_TITLE = "title"
        private const val RECIPE_IMG = "recipe_img"
        private const val RECIPE_TIME= "time"
        private const val RECIPE_RATE= "rate"
        private const val RECIPE_PROCEDURE= "procedure"
        private const val CATEGORY_FOREIGN_ID= "category_id"


        // recipe  ingredient table
        private const val TABLE_RECIPE_INGREDIENT = "ingredient"
        private const val INGREDIENT_ID= "ingredient_id"
        private const val INGREDIENT_NAME= "name"
        private const val INGREDIENT_AMOUNT= "amount"
        private const val INGREDIENT_FOREIGN_ID= "recipe_id"

        // images table
        private const val TABLE_IMG_CATEGORY = "image"
        private const val IMG_ID = "img_id"
        private const val IMG_DATA= "data"

    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys = ON;")
    }


    override fun onCreate(db: SQLiteDatabase?) {
        // USER TABLE
        db?.execSQL(
            "create table $TABLE_USER ($USER_ID integer primary key autoIncrement, $USER_NAME TEXT , $USER_EMAIL TEXT unique, $USER_PASSWORD TEXT, $USER_TYPE int default 0)"
        )
        db?.execSQL(
            "insert into $TABLE_USER($USER_EMAIL,$USER_NAME,$USER_PASSWORD,$USER_TYPE) values('ahmed@gmail.com','Ahmed Mohamed','ahmed1234',1)"
        )

        //create img table
        db?.execSQL(
            "create table $TABLE_IMG_CATEGORY ( $IMG_ID integer primary key autoIncrement , $IMG_DATA BLOB )"
        )

        //create category table
        db?.execSQL(
            "create table $TABLE_CATEGORY ($CATEGORY_ID integer primary key autoIncrement, $CATEGORY_TITLE TEXT,$IMG_FOREIGN_ID integer References $TABLE_IMG_CATEGORY ($IMG_ID) on delete cascade)"
        )

        //create recipe table
        db?.execSQL(
            "create table $TABLE_RECIPE ($RECIPE_ID integer primary key autoIncrement, $RECIPE_TITLE TEXT , $RECIPE_TIME integer,$RECIPE_RATE real,$RECIPE_PROCEDURE TEXT, $CATEGORY_FOREIGN_ID integer References $TABLE_CATEGORY ($CATEGORY_ID) ON DELETE CASCADE , $RECIPE_IMG  integer References $TABLE_IMG_CATEGORY ($IMG_ID) ON DELETE CASCADE)"
        )


        // crete recipe ingredient table
        db?.execSQL(
            "create table $TABLE_RECIPE_INGREDIENT ($INGREDIENT_ID integer primary key autoIncrement, $INGREDIENT_NAME TEXT , $INGREDIENT_AMOUNT integer ,$INGREDIENT_FOREIGN_ID integer REFERENCES $TABLE_RECIPE ($RECIPE_ID) on delete cascade )"
        )
   }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists $TABLE_USER")
        db?.execSQL("drop table if exists $TABLE_CATEGORY")
        db?.execSQL("drop table if exists $TABLE_RECIPE")
        db?.execSQL("drop table if exists $TABLE_RECIPE_INGREDIENT")
        db?.execSQL("drop table if exists $TABLE_IMG_CATEGORY")
        onCreate(db)
    }

    fun insertUser(user: User): Boolean {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()

        values.put(USER_NAME, user.name)
        values.put(USER_EMAIL, user.email)
        values.put(USER_PASSWORD, user.password)
        val res = db.insert(TABLE_USER, null, values)

        return res != -1L
    }

    @SuppressLint("Range")
    fun getUser(email: String, pass: String): Int {
        var found = 0
        val db = readableDatabase
        var userId = 0
        val args = arrayOf(email, pass)
        val cursor =
            db.rawQuery("select * from $TABLE_USER where $USER_EMAIL=? AND $USER_PASSWORD=?", args)
        if (cursor.moveToFirst() && cursor != null) {
            do {
                @SuppressLint("Range") val emailDb = cursor.getString(
                    cursor.getColumnIndex(
                        USER_EMAIL
                    )
                )
                @SuppressLint("Range") val passDb = cursor.getString(
                    cursor.getColumnIndex(
                        USER_PASSWORD
                    )
                )
                if (emailDb == email && passDb == pass) {
                    found = 1
                    userId = cursor.getInt(cursor.getColumnIndex(USER_ID))
                    break
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return if (found == 1 && userId != 0) {
            userId
        } else {
            -1
        }

    }


    fun getAllUsers(): ArrayList<User> {
        val db = readableDatabase
        val users = ArrayList<User>()
        val cursor =
            db.rawQuery("select * from $TABLE_USER", null)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(USER_NAME))
                @SuppressLint("Range") val email =
                    cursor.getString(cursor.getColumnIndex(USER_EMAIL))
                @SuppressLint("Range") val pass =
                    cursor.getString(cursor.getColumnIndex(USER_PASSWORD))
                val user = User(email, name, pass)
                users.add(user)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return users
    }

     fun getUserType(userId:Int):Int{
         val db = readableDatabase
         val args = arrayOf(userId.toString())
         var type2=0
         val cursor =
             db.rawQuery("select $USER_TYPE from $TABLE_USER where $USER_ID= ?", args)
         if (cursor!!.moveToFirst()) {
             @SuppressLint("Range")  val type =
                 cursor.getInt(cursor.getColumnIndex(USER_TYPE))
             type2=type
         }
         cursor.close()
         return type2
     }

// categories functions
fun getAllCategories(): MutableList<CategoryModel> {
    val db = readableDatabase
    val categories = ArrayList<CategoryModel>()
    val cursor =
        db.rawQuery("select * from $TABLE_CATEGORY inner join $TABLE_IMG_CATEGORY ON $IMG_FOREIGN_ID = $IMG_ID",null)
    if (cursor!!.moveToFirst()) {
        do {
            //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;
            @SuppressLint("Range") val category_title =
                cursor.getString(cursor.getColumnIndex(CATEGORY_TITLE))
            @SuppressLint("Range") val id =
                cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))

            @SuppressLint("Range") val category_img =
                cursor.getBlob(cursor.getColumnIndex(IMG_DATA))

            val category = CategoryModel(category_title,category_img,id)
            categories.add(category)
        } while (cursor.moveToNext())
        cursor.close()
    }
    return categories
}

    fun getAllCategoriesNames(): Map<Int,String>? {
        val db = readableDatabase
        val names= mutableMapOf<Int,String>()
        val cursor =
            db.rawQuery("select * from $TABLE_CATEGORY ",null)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;
                @SuppressLint("Range") val category_title =
                    cursor.getString(cursor.getColumnIndex(CATEGORY_TITLE))
                @SuppressLint("Range") val id =
                    cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))



                names.put(id,category_title)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return names
    }

    // recipes functions
    fun getAllRecipes(): MutableList<RecipeModel> {
        val db = readableDatabase
        val recipes = ArrayList<RecipeModel>()
        val cursor =
            db.rawQuery("select * from $TABLE_RECIPE inner join $TABLE_IMG_CATEGORY on $RECIPE_IMG = $IMG_ID ",null)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;
                @SuppressLint("Range") val recipe_title =
                    cursor.getString(cursor.getColumnIndex(RECIPE_TITLE))

                @SuppressLint("Range") val recipe_procedure =
                    cursor.getString(cursor.getColumnIndex(RECIPE_PROCEDURE))
                @SuppressLint("Range") val id =
                    cursor.getInt(cursor.getColumnIndex(RECIPE_ID))

                @SuppressLint("Range") val recipe_img =
                    cursor.getBlob(cursor.getColumnIndex(IMG_DATA))
                @SuppressLint("Range") val recipe_time =
                    cursor.getInt(cursor.getColumnIndex(RECIPE_TIME))
                @SuppressLint("Range") val recipe_rate =
                    cursor.getDouble(cursor.getColumnIndex(RECIPE_RATE))

                val recipe = RecipeModel(recipe_title,recipe_procedure,recipe_time,recipe_img,recipe_rate,id)
                recipes.add(recipe)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return recipes
    }

    fun getAllRecipesOfCategory(categoryId:Int): MutableList<RecipeModel> {
        val db = readableDatabase
        val recipes = ArrayList<RecipeModel>()
        val args= arrayOf(categoryId.toString())
        val cursor =
            db.rawQuery("select * from $TABLE_RECIPE inner join $TABLE_IMG_CATEGORY on $RECIPE_IMG = $IMG_ID where $CATEGORY_FOREIGN_ID= ?",args)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;
                @SuppressLint("Range") val recipe_title =
                    cursor.getString(cursor.getColumnIndex(RECIPE_TITLE))
                @SuppressLint("Range") val recipe_procedure =
                    cursor.getString(cursor.getColumnIndex(RECIPE_PROCEDURE))
                @SuppressLint("Range") val id =
                    cursor.getInt(cursor.getColumnIndex(RECIPE_ID))

                @SuppressLint("Range") val recipe_img =
                    cursor.getBlob(cursor.getColumnIndex(IMG_DATA))
                @SuppressLint("Range") val recipe_time =
                    cursor.getInt(cursor.getColumnIndex(RECIPE_TIME))
                @SuppressLint("Range") val recipe_rate =
                    cursor.getDouble(cursor.getColumnIndex(RECIPE_RATE))

                val recipe = RecipeModel(recipe_title,recipe_procedure,recipe_time,recipe_img,recipe_rate,id)
                recipes.add(recipe)
            } while (cursor.moveToNext())
            cursor.close()
        }
      return recipes
   }

    fun insertRecipe(recipe: RecipeModel, categoryId: Int):Boolean{
        val db: SQLiteDatabase= writableDatabase
        val values = ContentValues()

        values.put(RECIPE_TITLE,recipe.title);
        values.put(RECIPE_PROCEDURE,recipe.procedure);
        values.put(RECIPE_TIME,recipe.time);
        values.put(RECIPE_RATE,recipe.rate);
        values.put(RECIPE_IMG,getLastImgId());
        values.put(CATEGORY_FOREIGN_ID,categoryId);

        val res= db.insert(TABLE_RECIPE,null,values);
        return res != -1L
    }

    fun updateRecipe(recipeId: Int,recipe: RecipeModel): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(RECIPE_TITLE,recipe.title)
        values.put(RECIPE_PROCEDURE,recipe.procedure)
        values.put(RECIPE_TIME,recipe.time)

        values.put(RECIPE_IMG,getLastImgId());
        val res = db.update(
            TABLE_RECIPE,
            values,
            "$RECIPE_ID= ?",
            arrayOf<String>(recipeId.toString())
        )

        return res>0
    }


    fun deleteRecipe(id: Int):Boolean {
        val db = writableDatabase

        val args = arrayOf<String>(id.toString())
        val res = db.delete(
            TABLE_RECIPE,
            RECIPE_ID + "= ? ",
            args
        )

        return res > 0
    }






    fun getAllRecipeIngredients(recipeId: Int): MutableList<IngredientModel> {
        val db = readableDatabase
        val ingredients = ArrayList<IngredientModel>()
        val args= arrayOf(recipeId.toString())
        val cursor =
            db.rawQuery("select * from $TABLE_RECIPE_INGREDIENT where $RECIPE_ID= ?",args)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;
                @SuppressLint("Range") val ingredientName =
                    cursor.getString(cursor.getColumnIndex(INGREDIENT_NAME))
                @SuppressLint("Range") val ingredientId =
                    cursor.getInt(cursor.getColumnIndex(INGREDIENT_ID))

                @SuppressLint("Range") val ingredientAmount =
                    cursor.getInt(cursor.getColumnIndex(INGREDIENT_AMOUNT))


                val ingredient = IngredientModel(ingredientName,ingredientAmount,ingredientId)
                ingredients.add(ingredient)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return ingredients
    }

fun insertCategory(category: CategoryModel):Boolean{
    val db: SQLiteDatabase= writableDatabase
    var values = ContentValues()

    values.put(CATEGORY_TITLE,category.title);
    values.put(IMG_FOREIGN_ID,getLastImgId());

    val res= db.insert(TABLE_CATEGORY,null,values);
    return res != -1L
}

    fun insertImg(imgData:ByteArray):Boolean{
        val db: SQLiteDatabase= writableDatabase
        var values = ContentValues()

        values.put(IMG_DATA,imgData);

        val res= db.insert(TABLE_IMG_CATEGORY,null,values);
        return res != -1L
    }

    @SuppressLint("Range")
    fun getLastImgId():Int{
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("select MAX($IMG_ID) from $TABLE_IMG_CATEGORY",null)
        if (cursor.moveToFirst() && cursor.count>0) {
            return cursor.getInt(0)
        }else{
            return 0;
        }
    }

    fun getLastRecipeId():Int{
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("select MAX($RECIPE_ID) from $TABLE_RECIPE",null)
        if (cursor.moveToFirst() && cursor.count>0) {
            return cursor.getInt(0)
        }else{
            return 0;
        }
    }

        fun deleteCategory(id: Int):Boolean{
        val db = writableDatabase

        val args = arrayOf(id.toString())
        val res = db.delete(
            TABLE_CATEGORY,
            CATEGORY_ID + "= ? ",
            args
        )

        return res > 0

    }
//
    fun updateCategory(categoryId: Int,category: CategoryModel): Boolean {
        val db = writableDatabase
        val values = ContentValues()
    values.put(CATEGORY_TITLE,category.title);
    values.put(IMG_FOREIGN_ID,getLastImgId());
        val res = db.update(
            TABLE_CATEGORY,
            values,
            "$CATEGORY_ID= ?",
            arrayOf<String>(categoryId.toString())
        )

        return res>0
    }



    fun getCategoryImg(id:Int): ByteArray? {
        val db = readableDatabase
        var imgData1: ByteArray?=null
        val args = arrayOf(id.toString())

        val cursor =
            db.rawQuery("select * from $TABLE_IMG_CATEGORY left join $TABLE_CATEGORY on $IMG_FOREIGN_ID= $IMG_ID where $CATEGORY_ID= ?" ,args)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;


                @SuppressLint("Range") val imgData =
                    cursor.getBlob(cursor.getColumnIndex(IMG_DATA))
                imgData1= imgData
            } while (cursor.moveToNext())
            cursor.close()
        }

        if(imgData1==null){
            Log.d("from getCategoryImg function in db","null")

        }
        return imgData1
    }

    fun getRecipeImg(id:Int): ByteArray? {
        val db = readableDatabase
        var imgData1: ByteArray?=null
        val args = arrayOf(id.toString())

        val cursor =
            db.rawQuery("select * from $TABLE_IMG_CATEGORY left join $TABLE_RECIPE on $RECIPE_IMG= $IMG_ID where $RECIPE_ID= ?" ,args)
        if (cursor!!.moveToFirst()) {
            do {
                //@SuppressLint("Range") int id =cursor.getInt(cursor.getColumnIndex(USER_ID)) ;


                @SuppressLint("Range") val imgData =
                    cursor.getBlob(cursor.getColumnIndex(IMG_DATA))
                imgData1= imgData
            } while (cursor.moveToNext())
            cursor.close()
        }

        if(imgData1==null){
            Log.d("from getCategoryImg function in db","null")

        }
        return imgData1
    }


    @SuppressLint("Range")
    fun getLastCategoryId():Int{
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("select MAX($CATEGORY_ID) from $TABLE_CATEGORY",null)
        if (cursor.moveToFirst() && cursor.count>0) {
            return cursor.getInt(0)
        }else{
            return 0
        }
    }

    fun isImgExist(imgId:Int):Boolean{
        val db = readableDatabase
        val args = arrayOf(imgId.toString())

        val cursor:Cursor=db.rawQuery("select $IMG_DATA from $TABLE_IMG_CATEGORY where $IMG_ID = ? ",null)
        if (cursor.moveToFirst() && cursor.count>0) {
            return true
        }else{
            return false
        }
    }

    fun addIngredient(ingredient: IngredientModel, recipeId: Int):Boolean{
        val db: SQLiteDatabase= writableDatabase
        var values = ContentValues()

        values.put(INGREDIENT_NAME,ingredient.name)
        values.put(INGREDIENT_AMOUNT,ingredient.amount)
        values.put(INGREDIENT_FOREIGN_ID, recipeId)


        val res= db.insert(TABLE_RECIPE_INGREDIENT,null,values);
        return res != -1L
    }

    fun getLastIngredientId():Int{
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("select MAX($INGREDIENT_ID) from $TABLE_RECIPE_INGREDIENT ",null)
        if (cursor!!.moveToFirst() && cursor!=null) {
            val lastId= cursor.getInt(0)
            return lastId
        }else{
            return 0
        }
    }

    fun deleteIngredient(id: Int):Boolean{
        val db = writableDatabase

        val args = arrayOf<String>(id.toString())
        val res = db.delete(
            TABLE_RECIPE_INGREDIENT,
            INGREDIENT_ID + "= ? ",
            args
        )

        return res > 0

    }

//

}
