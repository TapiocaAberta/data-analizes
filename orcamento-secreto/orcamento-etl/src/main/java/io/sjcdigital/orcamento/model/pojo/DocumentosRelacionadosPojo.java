package io.sjcdigital.orcamento.model.pojo;

import java.util.List;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class DocumentosRelacionadosPojo {

    private Long idEmenda;
    private String draw;
    private String recordsTotal;
    private String recordsFiltered;
    
    private List<DocumentosDataPojo> data;

    /**
     * @return the draw
     */
    public String getDraw() {
        return draw;
    }

    /**
     * @param draw the draw to set
     */
    public void setDraw(String draw) {
        this.draw = draw;
    }

    /**
     * @return the data
     */
    public List<DocumentosDataPojo> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<DocumentosDataPojo> data) {
        this.data = data;
    }

    /**
     * @return the recordsTotal
     */
    public String getRecordsTotal() {
        return recordsTotal;
    }

    /**
     * @param recordsTotal the recordsTotal to set
     */
    public void setRecordsTotal(String recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    /**
     * @return the recordsFiltered
     */
    public String getRecordsFiltered() {
        return recordsFiltered;
    }

    /**
     * @param recordsFiltered the recordsFiltered to set
     */
    public void setRecordsFiltered(String recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    /**
     * @return the idEmenda
     */
    public Long getIdEmenda() {
        return idEmenda;
    }

    /**
     * @param idEmenda the idEmenda to set
     */
    public void setIdEmenda(Long idEmenda) {
        this.idEmenda = idEmenda;
    }

}
