export interface BasePolicy {
  id: number;
  name: string;
  description: string;
  premiumAmount: number;
  coverageAmount: number;
  durationMonths: number;
  renewalPremiumRate: number;
  category: string;
  createdAt: string | null;
}

export interface UserPolicy {
  id: number;
  policy: BasePolicy;
  startDate: string;
  endDate: string;
  status: string;
  premiumPaid: number;
}

export interface Claim {
  id: number;
  userPolicy?: UserPolicy;
  claimDate: string;
  claimAmount: number;
  reason: string;
  status: string;
  reviewerComment: string | null;
  resolvedDate: string | null;
}

export interface TicketData {
  subject: string;
  description: string;
  policyId: number | null;
  claimId: number | null;
}

export interface TicketClaimDetails {
  id?: number;
  userPolicyId?: number;
  claimAmount: number;
  reason: string;
  status?: string;
}

export interface SupportTicket {
  id: number;
  userId: number;
  userFullName?: string;
  policyId?: number | null;
  claimId?: number | null;
  policy?: BasePolicy | null;
  claim?: TicketClaimDetails | null;
  subject: string;
  description: string;
  status: 'OPEN' | 'RESOLVED' | 'CLOSED';
  response?: string;
  createdAt: string;
  resolvedAt?: string;
}
