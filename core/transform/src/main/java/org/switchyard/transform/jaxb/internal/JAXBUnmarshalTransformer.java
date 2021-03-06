/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.transform.jaxb.internal;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;

import org.jboss.logging.Logger;
import org.switchyard.Message;
import org.switchyard.SwitchYardException;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.internal.TransformMessages;

/**
 * JAXB Unmarshalling transformer.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 *
 * @param <F> From Type
 * @param <T> To Type.
 */
@Scannable(false)
public class JAXBUnmarshalTransformer<F, T> extends BaseTransformer<Message, Message> {

    private static Logger _logger = Logger.getLogger(JAXBUnmarshalTransformer.class);

    private JAXBContext _jaxbContext;
    private boolean _isXOPPackage;

    /**
     * Public constructor.
     * @param from From type.
     * @param to To type.
     * @param contextPath JAXB context path (Java package).
     * @param isXOPPackage true if XOPPackage is to be enabled.
     * @throws SwitchYardException Failed to create JAXBContext.
     */
    public JAXBUnmarshalTransformer(QName from, QName to, String contextPath, boolean isXOPPackage) throws SwitchYardException {
        super(from, to);
        _isXOPPackage = isXOPPackage;
        try {
            if (contextPath != null) {
                _jaxbContext = JAXBContext.newInstance(contextPath);
            } else {
                _jaxbContext = JAXBContext.newInstance(QNameUtil.toJavaMessageType(to));
            }
        } catch (JAXBException e) {
            throw TransformMessages.MESSAGES.failedToCreateJAXBContext(to.toString(), e);
        }
    }

    @Override
    public Message transform(Message message) {
        Unmarshaller unmarshaller;

        try {
            unmarshaller = _jaxbContext.createUnmarshaller();
            unmarshaller.setAttachmentUnmarshaller(new JAXBAttachmentUnmarshaller(message, _isXOPPackage));
        } catch (JAXBException e) {
            throw TransformMessages.MESSAGES.failedToCreateMarshaller(getTo().toString(), e);
        }

        try {
            byte[] bytes = null;
            if (InputStream.class.isAssignableFrom(message.getContent().getClass())
                    || Reader.class.isAssignableFrom(message.getContent().getClass())) {
                // Convert the stream/reader content to byte array first so it won't exhausted
                bytes = message.getContent(byte[].class);
                message.setContent(bytes);
            }
            Source source = message.getContent(Source.class);
            if (source instanceof StAXSource || source instanceof SAXSource) {
                // SWITCHYARD-2511 - Avoid StAXSource and SAXSource as those have issues on JAXB unmarshal
                if (bytes != null) {
                    message.setContent(bytes);
                }
                source = message.getContent(DOMSource.class);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("Unmarshalling from " + source.getClass() + ", systemId=" + source.getSystemId());
            }
            Object unmarshalledObject = unmarshaller.unmarshal(source);

            if (unmarshalledObject instanceof JAXBElement) {
                message.setContent(((JAXBElement)unmarshalledObject).getValue());
            } else {
                message.setContent(unmarshalledObject);
            }
        } catch (JAXBException e) {
            throw TransformMessages.MESSAGES.failedToUnmarshallForType(getTo().toString(), e);
        }

        return message;
    }
    
    class JAXBAttachmentUnmarshaller extends AttachmentUnmarshaller {

        private Message _message;
        private boolean _xop;
        
        JAXBAttachmentUnmarshaller(Message message, boolean xop) {
            _message = message;
            _xop = xop;
        }
        
        @Override
        public DataHandler getAttachmentAsDataHandler(String cid) {
            if (_message.getAttachment(cid) == null) {
                return null;
            }
            
            return new DataHandler(_message.getAttachment(cid));
        }

        @Override
        public byte[] getAttachmentAsByteArray(String cid) {
            if (_message.getAttachment(cid) == null) {
                return null;
            }
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                InputStream is = _message.getAttachment(cid).getInputStream();
                byte[] buff = new byte[128];
                int read;
                while ((read = is.read(buff)) != -1) {
                    os.write(buff, 0, read);
                }
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return os.toByteArray();
        }
        
        @Override
        public boolean isXOPPackage() {
            return _xop;
        }
    }

}
