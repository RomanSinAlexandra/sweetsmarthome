package com.example.sweetsmarthome.led

import com.google.android.gms.common.util.CollectionUtils.listOf

object LedFunction {

    val effects = listOf(
        LedEffect("Плавная радуга", "EFF:1"),
        LedEffect("Дыхание (Красный)", "EFF:2"),
        LedEffect("Дыхание (Зеленый)", "EFF:3"),
        LedEffect("Дыхание (Синий)", "EFF:4"),
        LedEffect("Стробоскоп", "EFF:5"),
        LedEffect("Резкая смена цветов", "EFF:6"),
        LedEffect("Случайные вспышки", "EFF:7"),
        LedEffect("Дыхание (Белый)", "EFF:8"),
        LedEffect("Дыхание (Желтый)", "EFF:9"),
        LedEffect("Дыхание (Голубой)", "EFF:10"),
        LedEffect("Дыхание (Пурпурный)", "EFF:11"),
        LedEffect("Дыхание (Случайный цвет)", "EFF:12"),
        LedEffect("Бегущая радуга", "EFF:13"),
        LedEffect("Резкая радуга", "EFF:14"),
        LedEffect("Фейерверк (Взрыв)", "EFF:15"),
        LedEffect("Переливание цветов", "EFF:16"),
        LedEffect("Флаг США", "EFF:17"),
        LedEffect("Запуск ракеты", "EFF:18"),
        LedEffect("Матрица", "EFF:19"),
        LedEffect("Пылающий огонь", "EFF:20"),
        LedEffect("Сканнер (Рыцарь дорог)", "EFF:21"),
        LedEffect("Звездное небо", "EFF:22"),
        LedEffect("Падающий метеорит", "EFF:23"),
        LedEffect("Полицейская мигалка", "EFF:24")
    )
}