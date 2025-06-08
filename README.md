# Projekt-JwZP

LINK DO POSTMANA:
https://app.getpostman.com/join-team?invite_code=ccb6cb3d9083ef754b9c082548dc1d88667562a708be0acd0253aea181cfecd0&target_code=6ca8d12d30df8facce64445338bffe8c

Pamietacz Miejscowy\
Opis\
Pamietacz Miejscowy to aplikacja, która pomaga użytkownikom zapamiętywać ciekawe miejsca i obiekty napotkane w codziennym życiu. Dzięki niej użytkownik może łatwo dodawać lokalizacje, przypisywać im kategorie i notatki oraz otrzymywać przypomnienia, gdy znajdzie się w pobliżu zapisanych miejsc. Aplikacja pozwala również na udostępnianie miejsc znajomym.\
Główne funkcje\
Zapisywanie lokalizacji – użytkownik może dodać miejsce do swojej listy na podstawie współrzędnych GPS.\
Kategorie i notatki – możliwość oznaczania miejsc różnymi kategoriami (np. „kawiarnia”, „sklep”, „jezioro”) i dodawania notatek.\
Udostępnianie miejsc – użytkownik może łatwo podzielić się zapisanym miejscem ze znajomymi.\
Dostęp do statystyk – użytkownik premium może przeglądać statystyki odwiedzanych miejsc.\
Typy użytkowników i ich uprawnienia\
Aplikacja obsługuje różne poziomy dostępu dla użytkowników:

Zwykły użytkownik\
Może dodawać i edytować swoje miejsca.\
Przegląda historię zapisanych lokalizacji.\
Udostępnia miejsca innym użytkownikom.\
Ograniczony dostęp do statystyk (np. liczba zapisanych miejsc).

Użytkownik premium\
Wszystkie funkcje zwykłego użytkownika.\
Dostęp do zaawansowanych statystyk odwiedzanych miejsc.\
Możliwość tworzenia prywatnych list miejsc widocznych tylko dla wybranych osób.

Administrator\
Ma pełną kontrolę nad aplikacją.\
Zarządza użytkownikami i ich uprawnieniami.\
Ma dostęp do wszystkich miejsc i ich historii.

Zewnętrzne API wykorzystywane w aplikacji.\
Pamietacz Miejscowy integruje się z następującymi API, aby wzbogacić funkcjonalność aplikacji:
-Google Maps API\
-OpenWeatherMap API

Dlaczego warto?\
Idealne dla osób, które często zapominają nazwy czy lokalizacje miejsc.\
Pomaga eksplorować miasto i odkrywać ukryte perełki.\
Ułatwia powrót do miejsc, które kiedyś się spodobały.\
Integracja z zewnętrznymi API sprawia, że aplikacja dostarcza dodatkowe informacje, które mogą być przydatne użytkownikowi.\
Użytkownicy premium zyskują dodatkowe funkcje, które ułatwiają organizację zapisanych lokalizacji.


**Wymagania projektowe:\
■ Domenę i zakres funkcjonalny należy ustalić z prowadzącym

Publiczne endpointy:\
POST /public/register – rejestracja nowego użytkownika\
GET /public/info - podstawowe informacje o aplikacji

Autoryzacja i zarządzanie użytkownikami\
GET /user/me – pobranie danych aktualnie zalogowanego użytkownika\
GET /user/friends – pobranie listy znajomych\
GET /user/user - informacje podstawowe o wszystkich uzytkownikach\
GET /user/{username} - informacje podstawowe o uzytkowniku na podstawie username\
PUT /user/update – aktualizacja zalogowanego danych użytkownika\
PATCH /user/password - aktualizacja hasła zalogowanego użytkownika\
POST /user/{username}/invite-friend - wyslij zaproszenie dla uzytkownika do znajomych\
DELETE /user/{username}/invite-friend - usun zaproszenie\
GET /user/invitations - sprawdz swoje zaproszenia do znjomych\
DELETE /user/{username}/delete-friend - usun znajomego\
POST /user/invitations/{username}/accept - zaakceptuj zaproszenie do znajomych\
DELETE /user/me – usunięcie konta\
GET /user/{id} -(tylko admin) informacje podstawowe o uzytkowniku na podstawie id \ 
DELETE /user/{id} -(tylko admin) usuwanie uzytkownika na podstawie id\
PATCH /user/{id}/role/{role} dodanie roli uzytkownikowi(tylko admin)\
DELETE /user/{id}/role/{role} (tylko admin) - usuniecie roli uzytkownika \
GET /user/account/status – sprawdzenie statusu premium


Zarządzanie miejscami\
POST /places – dodanie nowego miejsca\
GET /places/friend/{username}- pobranie informacji o publicznych miejscach przyjaciela\ 
GET /places – pobranie wszystkich miejsc użytkownika\
GET /places/private - pobranie wszystkich prywatny miejsc użytkownika\
GET /places/nearest - pobranie najbliższego miejsca użytkownika\
GET /places/nearest/{category} - pobranie najbliższego miejsca użytkownika wśród miejsc z podanej kategorii\
GET /places/{id} – pobranie szczegółów konkretnego miejsca\
PUT /places/{id} – edycja miejsca\
DELETE /places/{id} – usunięcie miejsca


Kategorie miejsc\
GET /categories – pobranie dostępnych kategorii\
POST /categories – dodanie nowej kategorii (tylko admin)\
DELETE /categories/{id} – usunięcie kategorii (tylko admin)


Udostępnianie miejsc\
POST /places/{id}/share – udostępnienie miejsca innemu użytkownikowi\
GET /places/shared – pobranie miejsc udostępnionych użytkownikowi

Monitorowanie systemu i logi\
GET /health – sprawdzenie stanu systemu  (tylko admin)\  
GET /logs/{date} – pobranie logów (tylko admin)


■ Kod źródłowy aplikacji należy utrzymywać na Githubie lub Gitlabie\
■ Kod powinien być automatycznie budowany (gradle lub maven), a wynik
budowania (pass/fail) powinien być widoczny w repozytorium.\
■ Kod powinien być automatycznie testowany, przynajmniej na dwóch
poziomach: jednostkowo i poprzez interfejs HTTP.\
○ Testowanie powinno być elementem budowania projektu.\
○ Wyniki testowania powinny być widoczne w repozytorium.\
■ Aplikację należy przetestować wydajnościowo z użyciem np Gatlinga.
Nie powinien to być element budowania automatycznego, ale powinno być
możliwe uruchomienie takiego testu jedną komendą.\
■ Kod powinien być automatycznie skanowany pod kątem bezpieczeństwa (np
Snyk).\
■ Dokumentację API należy udostępnić w formie Swaggera.\
■ Usługa powinna udostępniać poprawnie skonstruowane REST API, łącznie z
obsługą błędów.\
■ Musi być wykorzystywana data i czas, z poprawnym użyciem klasy Clock.
Funkcjonalność powinna obejmować strefy czasowe, które należy poprawnie
obsłużyć.\
■ Dependency Injection: należy używać wstrzykiwania przez konstruktor\
■ Należy używać bibliotekę do logów.\
■ Proces budowania powinien wytwarzać gotowy do wdrożenia artefakt: plik jar.\
■ Należy dostarczyć pliki: Dockerfile oraz Docker Compose, zawierające
kompletne środowisko uruchomieniowe.\
■ Usługa powinna zawierać persystencję. Baza danych w odrębnym Dockerfile,
połączona poprzez compose.\
■ Usługa powinna być monitorowana jakimś środowiskiem monitorującym, np
ELK. Środowisko to powinno być dostarczone w odrębnym Dockerfile,
połączone poprzez compose z pozostałymi komponentami.\
■ Usługa powinna korzystać z jakiegoś zewnętrznego, ogólnodostępnego
źródła danych w Internecie. Przykłady: OpenWheatherMap, JokeAPI, NASA
API, CoinGecko API, InPost API.\
■ Usługa powinna być zabezpieczona przynajmniej na podstawowym poziomie
(np logowanie basic auth, lub z użyciem autoryzacji Google)\
■ Usługa zawierać minimum 3 poziomy uprawnień użytkowników, z czego jeden
(user-admin) powinien służyć do zarządzania użytkownikami.\
■ Użytkownicy powinni mieć ograniczony dostęp do niektórych zasobów (np
tylko swoich).\
■ Należy przygotować demo serwisu, z użyciem np Postmana (i innych
narzędzi), które zademonstrują działanie i monitorowanie systemu.\
■ Wszelkie dodatkowe elementy ustalone z prowadzącym są również
obowiązkowe.**
