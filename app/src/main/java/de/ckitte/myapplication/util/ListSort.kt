package de.ckitte.myapplication.util

/**
 * Eine Enumeration gültiger Sortierungen.
 *
 * [ListSort.DateThenImportance] ==> Erst nach Datum, dann nach Wichtigkeit
 *
 * [ListSort.ImportanceThenDate] ==> Erst nach Wichtigkeit, dann nach Datum
 *
 * Erledigte Einträge stehen immer an letzter Stelle.
 */
enum class ListSort {
    DateThenImportance,
    ImportanceThenDate
}