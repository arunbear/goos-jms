package org.example.auctionsniper.swing;

import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
public class ComponentFinderTest {

    @Test
    public void it_can_find_a_component_by_name_and_type() {
        // given
        JPanel panel = new JPanel();
        JButton button = new JButton("Click Me");
        button.setName("myButton");
        panel.add(button);

        // when
        JButton result = ComponentFinder.findComponentByNameAsType(panel, "myButton", JButton.class);
        then(result).isSameAs(button);
    }

    @Test
    public void it_returns_empty_if_not_found() {
        // given
        JPanel panel = new JPanel();
        then(ComponentFinder.findComponentByName(panel, "nonexistent")).isEmpty();
    }

    @Test
    public void it_throws_on_type_mismatch() {
        // given
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Label");
        label.setName("myLabel");
        panel.add(label);

        // This should throw an assertion error because the type is wrong
        thenThrownBy(() -> ComponentFinder.findComponentByNameAsType(panel, "myLabel", JButton.class))
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining("should be of type JButton");
    }

    @Test
    public void it_can_find_components_in_nested_containers() {
        // given
        JPanel outer = new JPanel();
        JPanel inner = new JPanel();
        JButton button = new JButton("Nested");
        button.setName("nestedButton");
        inner.add(button);
        outer.add(inner);

        // when
        JButton result = ComponentFinder.findComponentByNameAsType(outer, "nestedButton", JButton.class);
        then(result).isSameAs(button);
    }
}
