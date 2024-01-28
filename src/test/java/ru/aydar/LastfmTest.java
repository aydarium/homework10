package ru.aydar;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@DisplayName("Проверки страниц Last.fm")
public class LastfmTest {
    @BeforeAll
    static void beforeAll() {
        Configuration.pageLoadStrategy = "eager";
    }

    @BeforeEach
    void beforeEach() {
        open("https://www.last.fm/");
    }

    @ParameterizedTest(name = "Проверка заголовка с названием исполнителя {0} на его странице")
    @ValueSource(strings = {
            "Elvis Costello", "Jamiroquai", "Ladytron"
    })
    void artistPageHeaderEqualsSearchQuery(String artist) {
        $(".masthead-search-toggle").click();
        $(".masthead-search-field").shouldBe(editable).setValue(artist).pressEnter();
        $(".grid-items-item").click();
        $(".header-new-title").shouldHave(text(artist));
    }

    @ParameterizedTest(name = "Проверка наличия альбома {1} в дискографии {0}")
    @CsvSource(value = {
            "The Prodigy , Music for the Jilted Generation",
            "Gorillaz , Clint Eastwood",
            "Soul Coughing , Irresistible Bliss"
    })
    void artistHasParticularRelease(String artist, String album) {
        $(".masthead-search-toggle").click();
        $(".masthead-search-field").shouldBe(editable).setValue(artist).pressEnter();
        $(".grid-items-item").click();
        $(byText("View all albums")).click();
        $("#artist-albums-section").shouldHave(text(album));
    }

    static Stream<Arguments> tracklists() {
        return Stream.of(
                Arguments.of(
                        "Hall & Oates",
                        "Maneater",
                        List.of("Maneater", "Delayed Reaction")
                ),
                Arguments.of(
                        "Pendulum",
                        "Elemental",
                        List.of("Driver", "Nothing For Free", "Louder Than Words", "Come Alive")
                ),
                Arguments.of(
                        "PJ Harvey",
                        "I Inside the Old I Dying",
                        List.of("I Inside the Old I Dying", "A Child's Question, August")
                )
        );
    }

    @ParameterizedTest(name = "Проверка треклиста альбома {1} исполнителя {0}")
    @MethodSource("tracklists")
    void albumHasCorrectTracklist(String artist, String album, List<String> tracklist) {
        $(".masthead-search-toggle").click();
        $(".masthead-search-field").shouldBe(editable).setValue(artist).pressEnter();
        $(".grid-items-item").click();
        $(byText("View all albums")).click();
        $("#artist-albums-section").$(byText(album)).click();
        $$(".chartlist-name").shouldHave(texts(tracklist));
    }
}
