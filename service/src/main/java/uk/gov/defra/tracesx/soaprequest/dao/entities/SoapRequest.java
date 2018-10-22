package uk.gov.defra.tracesx.soaprequest.dao.entities;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "soaprequest")
public class SoapRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String document;

  public SoapRequest() {}

  public SoapRequest(UUID id, String document) {
    this.id = id;
    this.document = document;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }
}
