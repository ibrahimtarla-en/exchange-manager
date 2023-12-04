package com.exchange.manager.exchangemanager.wallet.currency.service;

import com.exchange.manager.exchangemanager.wallet.currency.model.CurrencyDto;
import com.exchange.manager.exchangemanager.wallet.enums.Currencies;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements ICurrencyService {


    public NodeList getDocumentFromCentralBankAsNodeList(String url) {


        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(new URL(url).openStream());

            return document.getDocumentElement().getElementsByTagName("Currency");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Element getNodeFromNodeList(NodeList nodeList, Currencies currencies) {

        Node node = nodeList.item(currencies.value);

        if (node.getNodeType() == Node.ELEMENT_NODE) {

            Element element = (Element) node;


            return element;

        }
        return null;
    }

    @Override
    public List<CurrencyDto> getAllCurrenciesInfosFromCentralBank(String url) {

        return mapCurrentDtoListFromNodeList(getDocumentFromCentralBankAsNodeList(url));
    }

    public static List<CurrencyDto> mapCurrentDtoListFromNodeList(NodeList nodeList) {

        List<CurrencyDto> currencyDtoList = new ArrayList<>();

        List<Element> elements = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            } else return null;
        }
        elements.forEach(item -> {
            currencyDtoList.add(mapCurrencyDto(item));

        });
        return currencyDtoList;

    }

    public static CurrencyDto mapCurrencyDto(Element element) {

        Boolean checkBanknoteStatus = element.getElementsByTagName("BanknoteBuying").item(0).getTextContent().isBlank();


        if (!checkBanknoteStatus) {

            float currencyBuyingPrice = Float.parseFloat(element.getElementsByTagName("BanknoteBuying").item(0).getTextContent());
            float currencySellingPrice = Float.parseFloat(element.getElementsByTagName("BanknoteSelling").item(0).getTextContent());


            String currencyName = element.getElementsByTagName("CurrencyName").item(0).getTextContent();

            return CurrencyDto.builder()
                    .currencyType(currencyName)
                    .currencySellingPrice(currencySellingPrice)
                    .currencyBuyingPrice(currencyBuyingPrice)
                    .build();
        }


        String currencyName = element.getElementsByTagName("CurrencyName").item(0).getTextContent();

        return CurrencyDto.builder()
                .currencyType(currencyName)
                .build();
    }

    @Override
    public CurrencyDto getCurrencyInfosFromCentralBank(String url, Currencies currencies) {

        NodeList nodeList = getDocumentFromCentralBankAsNodeList(url);
        Element element = getNodeFromNodeList(nodeList, currencies);
        CurrencyDto currencyDto = mapCurrencyDto(element);
        return currencyDto;

    }
}
