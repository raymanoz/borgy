export interface Server {
    scales: string;
    trials: string;
    scale(name: string): string;
    trial(name: string): string;
    selectPreviousObservation(name: string): string;
    selectNextObservation(name: string): string;
}

const reactappserver = process.env.REACT_APP_SERVER;

export let server: Server = {
    scales: `${reactappserver}/api/scales`,
    scale(name: string): string {return `${this.scales}/${name}`; },
    trials: `${reactappserver}/api/trials`,
    trial(name: string): string {return `${this.trials}/${name}`; },
    selectPreviousObservation(name: string): string {return `${this.trial(name)}/selectPreviousObservation`; },
    selectNextObservation(name: string): string {return `${this.trial(name)}/selectNextObservation`; },
};
