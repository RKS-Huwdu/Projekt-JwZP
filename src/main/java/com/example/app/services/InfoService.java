package com.example.app.services;

import org.springframework.stereotype.Service;

@Service
public class InfoService {
    public String getAppInfo() {
        return "Pamietacz Miejscowy to aplikacja, która pomaga użytkownikom zapamiętywać ciekawe miejsca i obiekty napotkane w codziennym życiu.\n" +
                "Dzięki niej użytkownik może łatwo dodawać lokalizacje, przypisywać im kategorie i notatki oraz otrzymywać przypomnienia, gdy znajdzie się w pobliżu zapisanych miejsc.\n" +
                "Aplikacja pozwala również na udostępnianie miejsc znajomym.\n" +
                "Główne funkcje\n" +
                "Zapisywanie lokalizacji – użytkownik może dodać miejsce do swojej listy na podstawie współrzędnych GPS.\n" +
                "Kategorie i notatki – możliwość oznaczania miejsc różnymi kategoriami (np. „kawiarnia”, „sklep”, „jezioro”) i dodawania notatek.\n" +
                "Udostępnianie miejsc – użytkownik może łatwo podzielić się zapisanym miejscem ze znajomymi.\n" +
                "Dostęp do statystyk – użytkownik premium może przeglądać statystyki odwiedzanych miejsc.\n" +
                "Dlaczego warto?\n" +
                "Idealne dla osób, które często zapominają nazwy czy lokalizacje miejsc.\n" +
                "Pomaga eksplorować miasto i odkrywać ukryte perełki.\n" +
                "Ułatwia powrót do miejsc, które kiedyś się spodobały.\n" +
                "Integracja z zewnętrznymi API sprawia, że aplikacja dostarcza dodatkowe informacje, które mogą być przydatne użytkownikowi.\n" +
                "Użytkownicy premium zyskują dodatkowe funkcje, które ułatwiają organizację zapisanych lokalizacji.";
    }
}
