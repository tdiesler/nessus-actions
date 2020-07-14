package io.nessus.actions.model.converters;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.knowm.xchange.currency.CurrencyPair;

/*
 * [TODO] Remove when this is part of Camel
 * [CAMEL-15301] Provide various type converters for camel-xchange
 * https://issues.apache.org/jira/browse/CAMEL-15301 
 */
public class TickerTypeConverters implements TypeConverters {
	
    @Converter
    public CurrencyPair toCurrencyPair(String pair) {
        return new CurrencyPair(pair);
    }
}