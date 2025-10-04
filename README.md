# Cryptography

Projekt **Cryptography** to zbiór narzędzi i algorytmów kryptograficznych zaimplementowanych w języku Java. Repozytorium ma na celu edukację oraz umożliwienie eksperymentowania z różnymi technikami szyfrowania i deszyfrowania danych. Projekt powstał jako projekt na studia dla przemiotu Kryptografia.

## Spis treści

- [Opis](#opis)
- [Struktura projektu](#struktura-projektu)
- [Wymagania](#wymagania)
- [Licencja](#licencja)
- [Kontakt](#kontakt)

## Opis

W repozytorium znajdują się implementacje następujących algorytmów:
- Szyfr DES
- podpis Schnorr

## Struktura projektu

- `Model/`  
  - Zawiera logikę kryptograficzną i algorytmy.  
  - Plik konfiguracyjny Maven: [`Model/pom.xml`](https://github.com/eszymans/Cryptography/blob/main/Model/pom.xml)
  - Katalog źródłowy: [`Model/src`](https://github.com/eszymans/Cryptography/tree/main/Model/src)
- `View/`  
  - Odpowiada za warstwę prezentacji/graficzny interfejs użytkownika.  
  - Plik konfiguracyjny Maven: [`View/pom.xml`](https://github.com/eszymans/Cryptography/blob/main/View/pom.xml)
  - Katalog źródłowy: [`View/src`](https://github.com/eszymans/Cryptography/tree/main/View/src)
- [`pom.xml`](https://github.com/eszymans/Cryptography/blob/main/pom.xml) – główny plik konfiguracyjny Maven dla całego projektu
- [`Schnorr_AB_ES.zip`](https://github.com/eszymans/Cryptography/blob/main/Schnorr_AB_ES.zip) – dodatkowe materiały dotyczące protokołu Schnorra

## Wymagania

- Java 11 lub wyższa
- Maven

## Licencja

Projekt dostępny jest na licencji MIT.

## Kontakt

Autor: Edyta Szymańska, Alicja Bartczak

---