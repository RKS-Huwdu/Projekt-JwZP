2025-03-27

1. nazwa pakietu "DTOs" - nue użyamy wielkich liter w nazwach pakietów, proszę zmienić na "dtos"
- done
2. nie mogę uruchomić aplikacji lokalnie - nieprawidłowe hasło do Postgresa. Proszę dostarczyćdziałającą konfigurację, np z profilem "dev" i bazą in-meory, oraz opis uruchomienia.
- done teraz podczas startu automatycznie powinien sie uruchamiac profil dev zn baza H2, mozna tez uruchomic prod: ./gradlew bootRun --args='--spring.profiles.active=prod', albo zmieniajac spring.profiles.active w properties na prod
3. nie rozumiem jak działa API użytkowników oraz premium - ale może to kwestia endpointów i poczytania swaggera
4. Wygląda na dość duży postęp - tylko chciałbym bezproblemowo uruchomić :)