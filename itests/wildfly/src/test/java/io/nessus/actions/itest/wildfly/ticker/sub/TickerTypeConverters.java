package io.nessus.actions.itest.wildfly.ticker.sub;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

public class TickerTypeConverters implements TypeConverters {
	
    @Converter
    public CurrencyPair toCurrencyPair(String pair) {
        return new CurrencyPair(pair);
    }
	
    @Converter
    public ByteBuffer fromTicker(Ticker ticker) {
        BigDecimal last = ticker.getLast();
		return ByteBuffer.wrap(last.toString().getBytes());
    }
}