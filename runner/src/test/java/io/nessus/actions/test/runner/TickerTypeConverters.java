package io.nessus.actions.test.runner;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.knowm.xchange.currency.CurrencyPair;

public class TickerTypeConverters implements TypeConverters {
	
    @Converter
    public CurrencyPair toCurrencyPair(String pair) {
        return new CurrencyPair(pair);
    }
}