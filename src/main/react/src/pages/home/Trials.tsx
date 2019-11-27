import React, {Component} from "react";
import {connect} from "react-redux";
import {NavLink} from "react-router-dom";
import {AppState} from "../../store";
import {refreshTrials} from "../../store/activetrials/operations";
import {server} from "../../utils/server";
import {Trials, TrialSummary} from "../Scale";

interface Props {
    refreshTrials: () => void;

    trials: Trials;
}

class TrialsComponent extends Component<Props> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.renderTrial = this.renderTrial.bind(this);
        this.archiveTrial = this.archiveTrial.bind(this);
    }

    public componentDidMount() {
        this.props.refreshTrials();
    }

    public render() {
        return (
            <span id="trials">
                <h2>Trials</h2>
                {this.props.trials.map(this.renderTrial)}
            </span>
        );
    }

    private renderTrial(summary: TrialSummary, idx: number) {
        return <div key={idx} className="flex-group margin-bottom-1 padding-bottom-1 border-bottom">
            <NavLink to={"/trial/" + summary}>{`${summary.name}`}</NavLink> {summary.state}
            <div className="align-right">
                {summary.state === "ACTIVE" ?
                    <button className="primary" onClick={(_) => this.completeTrial(summary.name)}>Complete</button> :
                    <button className="primary" onClick={(_) => this.archiveTrial(summary.name)}>Archive</button>}
                {/*<button className="primary" onClick={(_) => this.exportTrial(summary.name)}>Export</button>*/}
            </div>
        </div>;
    }

    private archiveTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: "same-origin"})
            .then(() => this.props.refreshTrials());
    }

    private completeTrial(trialName: string) {
        fetch(server.completeTrial(trialName), {method: "POST", credentials: "same-origin"})
            .then(() => this.props.refreshTrials());
    }
/*

    private exportTrial(trialName: string) {
        fetch(server.completeTrial(trialName), {method: "POST", credentials: "same-origin"})
            .then(() => this.props.refreshTrials());
    }
*/

}

const mapStateToProps = (state: AppState) => ({
    trials: state.activeTrials,
});

export default connect(
    mapStateToProps,
    { refreshTrials },
)(TrialsComponent);
