// 초대장 클래스
public class Invitation {
    //초대일자 when 인스턴스 변수
    private LocalDateTime when;
}

// Ticket클래스: 공연을 관람하기 원하는 모든 사람들은 티켓을 소지해야함
public class Ticket {
    private Long fee;

    public Long getFee() {
        return fee;
    }
}

// 관람객이 가지고 올 수 있는  -> 가방 클래스
// 소지품: 초대장, 현금, 티켓

public class Bag {
    private Long amount;
    private Invitation invitation;
    private Ticket ticket;

    // 초대장의 보유 여부를 판단 hasInvitation()
    public boolean hasInvitation() {
        return invitation  != null;
    }

    // 티켓의 소유 여부 판단 hasTicket()
    public boolean hasTicket() {
        return ticket != null;
    }

    // 초대장을 티켓으로 교환하는: setTicket()
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    // 현금을 증가시키거나, 감소시키는: plusAmount(), minusAmout()
    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }

    // 현금과 초대장을 함께 보관하거나, 초대장 없이 현금만 보관하는 두 가지중 하나로
    // Bag의 인스턴스를 생성하는 시점에 제약을 강제할 수 있는 생성자 추가
    public Bag(long amount) {
        this(null, amount);
    }

    public Bag(Invitation invitation, long amount) {
        this.invitation = invitation;
        this.amount = amount;
    }

}

// 관람객 Audience 클래스
public class Audience {
    // 관람객은 소지품을 보관하기 위해 가방을 소지할 수 있다.
    private Bag bag;

    public Audience(Bag bag) {
        this.bag = bag;
    }

    public Bag getBag() {
        return bag;
    }
}

// 관람객이 소극장에 입장: 매표소에서 초대장을 티켓 교환 or 구매
// 매표소: 판매할 티켓, 판매금액 보관
// TicketOffice클래스
public class TicketOffice {
    private Long amount; //판매 금액 amount
    private List<Ticket> tickets = new ArrayList<>(); // 판매하거나 교환해줄 티켓의 목록 tickets

    public TicketOffice(Long amount, Ticket tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }

}
























