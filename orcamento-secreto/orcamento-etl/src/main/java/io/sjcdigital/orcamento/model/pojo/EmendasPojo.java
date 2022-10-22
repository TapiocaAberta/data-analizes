package io.sjcdigital.orcamento.model.pojo;

import java.util.List;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class EmendasPojo {
    
    private String draw;
    private String recordsTotal;
    private String recordsFiltered;
    
    private List<EmendasDataPojo> data;

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
     * @return the data
     */
    public List<EmendasDataPojo> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<EmendasDataPojo> data) {
        this.data = data;
    }

}
