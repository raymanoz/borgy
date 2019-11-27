export interface Intensity {
    number: number;
    label: string;
}

export interface Scale {
    name: string;
    description: string;
    intensities: Intensity[];
}

export interface TrialSummary {
    name: string;
    state: string;
}

export type Scales = Scale[];
export type Trials = TrialSummary[];
