package xyz.hisname.fireflyiii.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xyz.hisname.fireflyiii.repository.models.bills.BillAttributes
import xyz.hisname.fireflyiii.repository.models.bills.Relationships
import xyz.hisname.fireflyiii.repository.models.category.CategoryAttributes
import xyz.hisname.fireflyiii.repository.models.currency.CurrencyAttributes
import xyz.hisname.fireflyiii.repository.models.piggy.PiggyAttributes
import java.math.BigDecimal

object GsonConverterUtil{

    @TypeConverter
    @JvmStatic
    fun toBoolean(value: Boolean): String{
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromBoolean(value: String): Boolean{
        return Gson().fromJson(value, Boolean::class.java)

    }

    @TypeConverter
    @JvmStatic
    fun toBigDecimal(value: BigDecimal): String{
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromBigDecimal(value: String): BigDecimal{
        return Gson().fromJson(value, BigDecimal::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromString(value: List<String>): String{
        val type = object : TypeToken<List<String>>(){}.type
        return Gson().toJson(value,type)
    }

    @TypeConverter
    @JvmStatic
    fun toList(value: String): List<String>{
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value,type)
    }


    @TypeConverter
    @JvmStatic
    fun toCategoryAttribute(categoryAttributes: CategoryAttributes): String {
        return Gson().toJson(categoryAttributes)
    }

    @TypeConverter
    @JvmStatic
    fun fromCategoryAttributes(attributes: String): CategoryAttributes {
        return Gson().fromJson(attributes, CategoryAttributes::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun toCurrencyAttributes(currencyAttributes: CurrencyAttributes): String {
        return Gson().toJson(currencyAttributes)
    }

    @TypeConverter
    @JvmStatic
    fun fromCurrencyAttributes(attributes: String): CurrencyAttributes {
        return Gson().fromJson(attributes, CurrencyAttributes::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun toPiggyAttributes(piggyAttributes: PiggyAttributes): String {
        return Gson().toJson(piggyAttributes)
    }

    @TypeConverter
    @JvmStatic
    fun fromPiggyAttributes(attributes: String): PiggyAttributes {
        return Gson().fromJson(attributes, PiggyAttributes::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun toBillAttributes(billAttributes: BillAttributes): String {
        return Gson().toJson(billAttributes)
    }

    @TypeConverter
    @JvmStatic
    fun fromBillAttributes(attributes: String): BillAttributes {
        return Gson().fromJson(attributes, BillAttributes::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun toRelationShips(relationship: Relationships): String{
        return Gson().toJson(relationship)
    }

    @TypeConverter
    @JvmStatic
    fun fromRelationShips(relationships: String): Relationships {
        return Gson().fromJson(relationships, Relationships::class.java)
    }

}

