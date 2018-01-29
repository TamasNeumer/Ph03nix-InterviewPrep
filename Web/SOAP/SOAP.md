# SOAP

#### Intro
- SOAP = Simple Object Access Protocol
- It is an **XML-based messaging protocol** for exchanging information among computers.

#### Messages
- A SOAP message is an ordinary XML document containing the following elements −
  - **Envelope** − Defines the start and the end of the message. It is a mandatory element.
  - **Header** − Contains any optional attributes of the message used in processing the message, either at an intermediary point or at the ultimate end-point. It is an optional element.
  - **Body** − Contains the XML data comprising the message being sent. It is a mandatory element.
  - **Fault** − An optional Fault element that provides information about errors that occur while processing the message.

**Envelope**
- The SOAP envelope indicates the start and the end of the message so that the receiver knows when an entire message has been received. The SOAP envelope solves the problem of knowing when you are done receiving a message and are ready to process it. The SOAP envelope is therefore basically a packaging mechanism.
- Every Envelope element must contain exactly one Body element.
- If an Envelope contains a Header block, it must contain no more than one, and it must appear as the first child of the Envelope, before the Body. (Header block != header element.)

**Header**
- The optional Header element offers a flexible framework for specifying additional application-level requirements. For example, the Header element can be used to specify a digital signature for password-protected services.
- When multiple headers are defined, all immediate child elements of the SOAP header are interpreted as SOAP header blocks.

**Body**
- The SOAP body is a mandatory element that contains the application-defined XML data being exchanged in the SOAP message.
- The body is defined as a child element of the envelope, and the semantics for the body are defined in the associated SOAP schema. The body contains mandatory information intended for the ultimate receiver of the message.
