# FSM Designer & Simulator

### Proje Özeti
Bu proje 1. sınıf Programlamaya Giriş 2 dersi projesi için, Java ile geliştirilmiş kapsamlı bir Sonlu Durum Makinesi (Finite State Machine - FSM) tasarım ve simülasyon aracıdır. Kullanıcıların terminal üzerinden kendi otomat modellerini tanımlamasına, yönetmesine ve test etmesine olanak tanır.

## Öne Çıkan Özellikler:

Dinamik Tanımlama: Komut satırı üzerinden alfabe (symbols), durumlar (states) ve geçiş kuralları (transitions) tanımlama.

Simülasyon Motoru: Girilen dizgilerin (input strings) makine tarafından kabul edilip edilmediğini adım adım izleme ve YES/NO sonucu üretme.

Veri Kalıcılığı: Tasarlanan modelleri insan tarafından okunabilir metin (.txt) veya Java Serialization kullanarak binary (.fs) formatında kaydetme ve geri yükleme.

Gelişmiş Günlükleme: Tüm işlem geçmişini ve hataları Logger yapısı ile harici dosyalara kaydetme.

### Project Overview
This project is a comprehensive Finite State Machine (FSM) design and simulation tool developed in Java for the 1st-year Introduction to Programming 2 course. It allows users to define, manage, and test their own automaton models via a terminal.

## Key Features:

Dynamic Configuration: Define alphabets, states, and transition rules directly via terminal commands.

Simulation Engine: Validates input strings against the defined FSM with step-by-step path tracking and acceptance (YES/NO) results.

Data Persistence: Support for saving/loading FSM designs in both human-readable text (.txt) and binary (.fs) formats via Java Serialization.

Advanced Logging: Integrated Logger system to record operations and system messages to external files.
